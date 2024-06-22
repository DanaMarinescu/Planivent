package com.event.eventapp.service;

import com.event.eventapp.DTO.UserDTO;
import com.event.eventapp.DTO.UserRegisteredDTO;
import com.event.eventapp.exceptions.EmailExistsException;
import com.event.eventapp.model.Role;
import com.event.eventapp.model.User;
import com.event.eventapp.repository.RoleRepository;
import com.event.eventapp.repository.UserProjection;
import com.event.eventapp.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service("defaultUserService")
@Primary
public class DefaultUserServiceImpl implements DefaultUserService {
    @Override
    public User findByName(String name) {
        return userRepo.findByEmail(name);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public User findById(Long id) { return userRepo.findById(id).get(); }

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DefaultUserServiceImpl(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Datele introduse sunt invalide.");
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true, true, true, true,
                AuthorityUtils.createAuthorityList(user.getRole().getRole()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getRole()));
    }

    @Override
    public User save(UserRegisteredDTO userRegisteredDTO) throws EmailExistsException {
        if (emailExists(userRegisteredDTO.getEmail())) {
            throw new EmailExistsException("Există deja un cont cu această adresă de email: " + userRegisteredDTO.getEmail());
        }

        Role role = roleRepo.findByRole(userRegisteredDTO.getRole());
        if (role == null) {
            throw new IllegalStateException("Role does not exist");
        }
        User user = new User();
        user.setEmail(userRegisteredDTO.getEmail());
        user.setName(userRegisteredDTO.getName());
        user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
        user.setRole(role);

        return userRepo.save(user);
    }

    public boolean emailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public Long getUserIdByName(String username) { return userRepo.findByEmail(username).getId(); }
}
