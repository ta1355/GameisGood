package game.gamegoodgood.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/user/create")
    public ResponseEntity<UsersDTO> createUser(@RequestBody UsersDTO dto){
        usersService.createUser(dto);
        return ResponseEntity.ok(dto);
    }


}
