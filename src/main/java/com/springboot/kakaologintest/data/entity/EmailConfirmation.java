package com.springboot.kakaologintest.data.entity;

        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;

        import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmailConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailId;

    @Column
    private String personalEmail;

    @Column
    private String confirmationCode;

    @Column
    private boolean isVerified = false;

    @OneToOne
    @JoinColumn(name = "uid")
    private User user;
}
