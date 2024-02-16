package org.prgms.locomocoserver.user.domain;

import java.util.List;
import org.prgms.locomocoserver.mogakkos.domain.Mogakko;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Optional<User> findByNicknameAndDeletedAtIsNull(String nickname);
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT p.user.id FROM Participant p WHERE p.mogakko = :mogakko)") // 검증 필
    List<User> findAllParticipantsByMogakko(Mogakko mogakko);
}
