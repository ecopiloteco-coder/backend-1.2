import org.springframework.web.bind.annotation.DeleteMapping;
import com.ecopilot.user.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/sync")
public class UserSyncController {
    private final UserService userService;

    public UserSyncController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Webhook appelé par Keycloak lors de la suppression d'un utilisateur.
     * @param keycloakId l'identifiant Keycloak de l'utilisateur supprimé
     */
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUserByKeycloakId(@RequestParam String keycloakId) {
        userService.deleteUserByKeycloakId(keycloakId);
        return ResponseEntity.ok().build();
    }
}