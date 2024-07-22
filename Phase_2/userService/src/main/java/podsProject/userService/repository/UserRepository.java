package podsProject.userService.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import podsProject.userService.entity.User;

import java.util.Optional;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
