package com.umograd.presentation.moderator;

import com.umograd.application.user.command.DeleteUserCommand;
import com.umograd.application.user.command.DeleteUserCommandHandler;
import com.umograd.application.user.command.UpdateUserRoleCommand;
import com.umograd.application.user.command.UpdateUserRoleCommandHandler;
import com.umograd.application.user.query.GetAllUsersQuery;
import com.umograd.application.user.query.GetAllUsersQueryHandler;
import com.umograd.presentation.common.mapper.UserMapper;
import com.umograd.presentation.moderator.dto.UpdateUserRoleRequest;
import com.umograd.presentation.moderator.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator/users")
@RequiredArgsConstructor
public class ModeratorUserController {

    private final GetAllUsersQueryHandler getAllUsersQueryHandler;
    private final UpdateUserRoleCommandHandler updateUserRoleCommandHandler;
    private final DeleteUserCommandHandler deleteUserCommandHandler;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return getAllUsersQueryHandler.handle(new GetAllUsersQuery()).stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }

    @PutMapping("/{id}/role")
    public UserResponse updateUserRole(@PathVariable Long id, @RequestBody UpdateUserRoleRequest request) {
        return UserMapper.toUserResponse(
                updateUserRoleCommandHandler.handle(new UpdateUserRoleCommand(id, request.role()))
        );
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        deleteUserCommandHandler.handle(new DeleteUserCommand(id));
    }
}
