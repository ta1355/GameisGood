package game.gamegoodgood.config.auth;

import game.gamegoodgood.user.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrincipalDetails implements UserDetails {

    private Users users;

    public PrincipalDetails(Users users){
        this.users = users;
    }


    //해당 user의 권한을 리턴하는 곳!!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return users.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return users.getUserPassword();
    }

    @Override
    public String getUsername() {
        return users.getUsername();
    }

    @Override
    public boolean isAccountNonExpired(){
        return  true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return  false;
    }

    // 우리 사이트!! 1년동안 회원이 로그인 안하면!! 휴먼으로 강등!!
    @Override
    public boolean isEnabled(){

        // 현재시간 - 로긴시간 => 1년을 초과하면 return false;
        users.getLoginDate();
        return true;
    }


}
