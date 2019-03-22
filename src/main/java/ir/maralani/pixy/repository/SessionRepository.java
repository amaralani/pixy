package ir.maralani.pixy.repository;

import ir.maralani.pixy.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author amir
 *
 * JPA Repository to handle CRUD and queries for {@link Session}
 */

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {

}
