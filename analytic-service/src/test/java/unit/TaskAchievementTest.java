package unit;

import com.umograd.analytic.dto.AchievementGrantResponse;
import com.umograd.analytic.entity.AchievementEntity;
import com.umograd.analytic.entity.ChildAchievementEntity;
import com.umograd.analytic.entity.task.TaskResultEntity;
import com.umograd.analytic.mapper.AchievementMapper;
import com.umograd.analytic.repository.analytic.AchievementRepository;
import com.umograd.analytic.repository.analytic.ChildAchievementRepository;
import com.umograd.analytic.repository.task.TaskResultRepository;
import com.umograd.analytic.service.impl.DefaultAchievementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskAchievementTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private ChildAchievementRepository childAchievementRepository;

    @Mock
    private TaskResultRepository taskResultRepository;

    @Mock
    private AchievementMapper achievementMapper;

    @InjectMocks
    private DefaultAchievementService service;

    private final Long childId = 1L;

    private AchievementEntity createAchievement(Long id, String type, Integer value) {
        AchievementEntity a = new AchievementEntity();
        a.setId(id);
        a.setConditionType(type);
        a.setConditionValue(value);
        return a;
    }

    @Test
    void checkAndGrantShouldGrantAchievementWhenConditionsAreMet() {
        AchievementEntity achievement = createAchievement(10L, "CONSECUTIVE_CORRECT", 2);
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        when(childAchievementRepository.existsByChildIdAndAchievementId(childId, 10L)).thenReturn(false);

        TaskResultEntity r1 = mock(TaskResultEntity.class);
        when(r1.getStatus()).thenReturn("DONE");
        when(r1.getScore()).thenReturn(100);

        TaskResultEntity r2 = mock(TaskResultEntity.class);
        when(r2.getStatus()).thenReturn("DONE");
        when(r2.getScore()).thenReturn(120);

        when(taskResultRepository.findLastResultsWithWindow(childId, 2)).thenReturn(List.of(r1, r2));

        AchievementGrantResponse response = mock(AchievementGrantResponse.class);
        when(achievementMapper.toGrantResponse(achievement)).thenReturn(response);

        List<AchievementGrantResponse> result = service.checkAndGrant(childId);

        assertEquals(1, result.size());
        assertSame(response, result.get(0));

        ArgumentCaptor<ChildAchievementEntity> captor = ArgumentCaptor.forClass(ChildAchievementEntity.class);
        verify(childAchievementRepository, times(1)).save(captor.capture());

        ChildAchievementEntity saved = captor.getValue();
        assertEquals(childId, saved.getChildId());
        assertSame(achievement, saved.getAchievement());
        assertNotNull(saved.getEarnedAt());
    }

    @Test
    void checkAndGrantShouldNotGrantWhenAlreadyEarned() {
        AchievementEntity achievement = createAchievement(10L, "CONSECUTIVE_CORRECT", 2);
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        when(childAchievementRepository.existsByChildIdAndAchievementId(childId, 10L)).thenReturn(true);

        List<AchievementGrantResponse> result = service.checkAndGrant(childId);

        assertTrue(result.isEmpty());
        verifyNoInteractions(taskResultRepository, achievementMapper);
        verify(childAchievementRepository, never()).save(any());
    }

    @Test
    void checkAndGrantShouldNotGrantWhenUnknownConditionType() {
        AchievementEntity achievement = createAchievement(10L, "UNKNOWN_TYPE", 2);
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        when(childAchievementRepository.existsByChildIdAndAchievementId(childId, 10L)).thenReturn(false);

        List<AchievementGrantResponse> result = service.checkAndGrant(childId);

        assertTrue(result.isEmpty());
        verifyNoInteractions(taskResultRepository, achievementMapper);
        verify(childAchievementRepository, never()).save(any());
    }

    @Test
    void checkAndGrantShouldNotGrantWhenNotEnoughResults() {
        AchievementEntity achievement = createAchievement(10L, "CONSECUTIVE_CORRECT", 3);
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        when(childAchievementRepository.existsByChildIdAndAchievementId(childId, 10L)).thenReturn(false);

        TaskResultEntity r1 = mock(TaskResultEntity.class);
        when(taskResultRepository.findLastResultsWithWindow(childId, 3)).thenReturn(List.of(r1));

        List<AchievementGrantResponse> result = service.checkAndGrant(childId);

        assertTrue(result.isEmpty());
        verify(childAchievementRepository, never()).save(any());
        verifyNoInteractions(achievementMapper);
    }

    @Test
    void checkAndGrantShouldNotGrantWhenStatusIsNotDone() {
        AchievementEntity achievement = createAchievement(10L, "CONSECUTIVE_CORRECT", 1);
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        when(childAchievementRepository.existsByChildIdAndAchievementId(childId, 10L)).thenReturn(false);

        TaskResultEntity r1 = mock(TaskResultEntity.class);
        when(r1.getStatus()).thenReturn("FAILED");

        when(taskResultRepository.findLastResultsWithWindow(childId, 1)).thenReturn(List.of(r1));

        List<AchievementGrantResponse> result = service.checkAndGrant(childId);

        assertTrue(result.isEmpty());
        verify(childAchievementRepository, never()).save(any());
    }

    @Test
    void checkAndGrantShouldNotGrantWhenScoreIsLow() {
        AchievementEntity achievement = createAchievement(10L, "CONSECUTIVE_CORRECT", 1);
        when(achievementRepository.findAll()).thenReturn(List.of(achievement));
        when(childAchievementRepository.existsByChildIdAndAchievementId(childId, 10L)).thenReturn(false);

        TaskResultEntity r1 = mock(TaskResultEntity.class);
        when(r1.getStatus()).thenReturn("DONE");
        when(r1.getScore()).thenReturn(99);

        when(taskResultRepository.findLastResultsWithWindow(childId, 1)).thenReturn(List.of(r1));

        List<AchievementGrantResponse> result = service.checkAndGrant(childId);

        assertTrue(result.isEmpty());
        verify(childAchievementRepository, never()).save(any());
    }

    @Test
    void getEarnedAchievementIdsShouldReturnMappedIds() {
        AchievementEntity a1 = createAchievement(100L, "TYPE", 1);
        AchievementEntity a2 = createAchievement(200L, "TYPE", 2);

        ChildAchievementEntity ca1 = new ChildAchievementEntity();
        ca1.setAchievement(a1);
        ChildAchievementEntity ca2 = new ChildAchievementEntity();
        ca2.setAchievement(a2);

        when(childAchievementRepository.findAllByChildId(childId)).thenReturn(List.of(ca1, ca2));

        List<Long> result = service.getEarnedAchievementIds(childId);

        assertEquals(2, result.size());
        assertTrue(result.contains(100L));
        assertTrue(result.contains(200L));
    }

    @Test
    void getEarnedAchievementIdsShouldReturnEmptyListWhenNoAchievements() {
        when(childAchievementRepository.findAllByChildId(childId)).thenReturn(Collections.emptyList());

        List<Long> result = service.getEarnedAchievementIds(childId);

        assertTrue(result.isEmpty());
    }
}
