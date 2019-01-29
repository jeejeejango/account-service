package org.jeejeejango.repository;

import org.jeejeejango.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jeejeejango
 * @since 29/01/2019 11:58 PM
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserIdIn(List<Long> userId, Pageable pageable);


    List<Account> findByUserIdInAndAccountNameContainingIgnoreCase(List<Long> userId, String accountName, Pageable pageable);


    List<Account> findByAccountNameContainingIgnoreCase(String accountName, Pageable pageable);


}
