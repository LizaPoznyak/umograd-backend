package com.umograd.presentation.parent;

import com.umograd.application.user.command.CreateChildCommand;
import com.umograd.application.user.command.CreateChildCommandHandler;
import com.umograd.application.user.command.DeleteChildCommand;
import com.umograd.application.user.command.DeleteChildCommandHandler;
import com.umograd.application.user.query.FindUserByUsernameQuery;
import com.umograd.application.user.query.FindUserByUsernameQueryHandler;
import com.umograd.application.user.query.GetChildrenQuery;
import com.umograd.application.user.query.GetChildrenQueryHandler;
import com.umograd.domain.user.User;
import com.umograd.presentation.common.mapper.UserMapper;
import com.umograd.presentation.parent.dto.ChildResponse;
import com.umograd.presentation.parent.dto.CreateChildRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parent/children")
@RequiredArgsConstructor
public class ParentChildController {

    private final CreateChildCommandHandler createChildCommandHandler;
    private final GetChildrenQueryHandler getChildrenQueryHandler;
    private final DeleteChildCommandHandler deleteChildCommandHandler;
    private final FindUserByUsernameQueryHandler findUserByUsernameQueryHandler;

    @PostMapping
    public ChildResponse createChild(@AuthenticationPrincipal UserDetails parentDetails,
                                     @RequestBody CreateChildRequest request) {
        User parent = findUserByUsernameQueryHandler.handle(new FindUserByUsernameQuery(parentDetails.getUsername()));
        return UserMapper.toChildResponse(
                createChildCommandHandler.handle(new CreateChildCommand(parent.getId(), request.username(), request.email(), request.password()))
        );
    }

    @GetMapping
    public List<ChildResponse> getChildren(@AuthenticationPrincipal UserDetails parentDetails) {
        User parent = findUserByUsernameQueryHandler.handle(new FindUserByUsernameQuery(parentDetails.getUsername()));
        return getChildrenQueryHandler.handle(new GetChildrenQuery(parent.getId())).stream()
                .map(UserMapper::toChildResponse)
                .toList();
    }

    @DeleteMapping("/{childId}")
    public void deleteChild(@AuthenticationPrincipal UserDetails parentDetails,
                            @PathVariable Long childId) {
        User parent = findUserByUsernameQueryHandler.handle(new FindUserByUsernameQuery(parentDetails.getUsername()));
        deleteChildCommandHandler.handle(new DeleteChildCommand(parent.getId(), childId));
    }
}
