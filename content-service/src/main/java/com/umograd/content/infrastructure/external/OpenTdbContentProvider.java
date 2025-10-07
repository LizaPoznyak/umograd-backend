package com.umograd.content.infrastructure.external;

import com.umograd.content.domain.external.ContentProvider;
import com.umograd.content.domain.external.ExternalTaskContentDto;
import com.umograd.content.domain.external.ExternalTaskDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component("opentdb")
public class OpenTdbContentProvider implements ContentProvider {

    private final RestTemplate restTemplate;

    public OpenTdbContentProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<ExternalTaskDto> fetchTasks(String topic, int limit) {
        // Получаем categoryId по названию темы
        int categoryId = OpenTdbCategories.CATEGORY_MAP.getOrDefault(topic, 9);

        String url = "https://opentdb.com/api.php?amount=" + limit +
                "&category=" + categoryId +
                "&type=multiple";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        if (results == null || results.isEmpty()) {
            return List.of();
        }

        return results.stream().map(q -> {
            String question = (String) q.get("question");
            String correct = (String) q.get("correct_answer");
            List<String> incorrect = (List<String>) q.get("incorrect_answers");

            return new ExternalTaskDto(
                    "opentdb-" + topic,
                    "Trivia Question",
                    "Вопрос из OpenTDB",
                    10, 99,
                    ((String) q.get("difficulty")).toUpperCase(),
                    List.of(topic),
                    List.of(),
                    null,
                    new ExternalTaskContentDto(
                            "quiz",
                            question,
                            mergeAnswers(correct, incorrect),
                            correct
                    )
            );
        }).toList();
    }

    private List<String> mergeAnswers(String correct, List<String> incorrect) {
        List<String> all = new java.util.ArrayList<>(incorrect);
        all.add(correct);
        java.util.Collections.shuffle(all);
        return all;
    }
}
