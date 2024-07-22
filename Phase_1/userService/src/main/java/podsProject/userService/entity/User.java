package podsProject.userService.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "app_user")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "id")
    @SequenceGenerator(name = "id", initialValue=1,allocationSize = 1)
    Integer id;
    @Column(name = "name",nullable = false)
    String name;
    @Column(name = "email",unique = true)
    String email;
}
