package com.pharmacy.auth.service;

import com.pharmacy.auth.client.MemberDataClient;
import com.pharmacy.auth.client.MemberDataClient.MemberDataErasureException;
import com.pharmacy.auth.entity.User;
import com.pharmacy.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * User accounts against the real (H2) database: uniqueness ignores case, a
 * sign-up that loses a race gets a clean error, and deleting a user erases
 * their data in the other services first.
 */
@SpringBootTest(properties = "jwt.secret=test-secret-0123456789-0123456789-0123456789-0123456789-0123456789")
class UserAccountIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @MockitoSpyBean
    private UserRepository userRepository;
    
    @MockitoBean
    private MemberDataClient memberDataClient;
    
    private User create(String username, String email) {
        return userService.createUser(username, email, "password1", "Test User");
    }
    
    @Test
    void usernamesAreUniqueRegardlessOfCase() {
        create("CaseBob", "casebob@example.com");
        
        assertThatThrownBy(() -> create("casebob", "other.bob@example.com"))
            .isInstanceOf(UserAccountException.class)
            .hasMessage("Error: Username is already taken!");
        assertThat(userService.existsByUsername("CASEBOB")).isTrue();
    }
    
    @Test
    void emailsAreStoredInLowerCaseAndUniqueRegardlessOfCase() {
        User user = create("mailer", "  Mixed.Case@Example.COM ");
        assertThat(user.getEmail()).isEqualTo("mixed.case@example.com");
        
        assertThatThrownBy(() -> create("mailer2", "MIXED.case@example.com"))
            .isInstanceOf(UserAccountException.class)
            .hasMessage("Error: Email is already in use!");
        assertThat(userService.existsByEmail("Mixed.Case@example.com")).isTrue();
    }
    
    @Test
    void usersCanChangeTheCaseOfTheirOwnNameButNotTakeSomeoneElses() {
        User user = create("renameMe", "rename@example.com");
        create("taken", "taken@example.com");
        
        User updated = userService.updateUser(user.getId(), "RENAMEME", "Rename@Example.com", null, null, null);
        assertThat(updated.getUsername()).isEqualTo("RENAMEME");
        assertThat(updated.getEmail()).isEqualTo("rename@example.com");
        
        assertThatThrownBy(() -> userService.updateUser(user.getId(), "TAKEN", "rename@example.com", null, null, null))
            .isInstanceOf(UserAccountException.class)
            .hasMessage("Error: Username is already taken!");
    }
    
    @Test
    void aSignUpThatLosesARaceGetsACleanError() {
        create("racer", "racer@example.com");
        // The competing request passed its "is it taken?" checks before the first one saved.
        doReturn(false).when(userRepository).existsByUsernameIgnoreCase("racer");
        
        assertThatThrownBy(() -> create("racer", "racer2@example.com"))
            .isInstanceOf(UserAccountException.class)
            .hasMessage("Error: Username or email is already taken!");
        assertThat(userRepository.existsByEmailIgnoreCase("racer2@example.com")).isFalse();
    }
    
    @Test
    void aSimultaneousSignUpDifferingOnlyInCaseIsStoppedByTheDatabase() {
        create("Twin", "twin.one@example.com");
        // Both requests passed the "is it taken?" check before either saved.
        doReturn(false).when(userRepository).existsByUsernameIgnoreCase("twin");
        
        assertThatThrownBy(() -> create("twin", "twin.two@example.com"))
            .isInstanceOf(UserAccountException.class)
            .hasMessage("Error: Username or email is already taken!");
    }
    
    @Test
    void deletingAUserErasesTheirDataBeforeTheAccount() {
        User user = create("leaver", "leaver@example.com");
        
        assertThat(userService.deleteUser(user.getId())).isTrue();
        
        var order = inOrder(memberDataClient, userRepository);
        order.verify(memberDataClient).eraseMemberData(user.getMemberId());
        order.verify(userRepository).deleteById(user.getId());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
    
    @Test
    void aUserIsKeptWhenTheirDataCannotBeErased() {
        User user = create("stayer", "stayer@example.com");
        doThrow(new MemberDataErasureException("refill", new RuntimeException("connection refused")))
            .when(memberDataClient).eraseMemberData(user.getMemberId());
        
        assertThatThrownBy(() -> userService.deleteUser(user.getId()))
            .isInstanceOf(MemberDataErasureException.class);
        assertThat(userRepository.findById(user.getId())).isPresent();
    }
    
    @Test
    void deletingAnUnknownUserReportsNotFound() {
        assertThat(userService.deleteUser(999_999L)).isFalse();
        verifyNoInteractions(memberDataClient);
    }
}
