package org.sdn.walletservice.repository;

import org.sdn.walletservice.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Wallet findByContact(String contact);

    @Modifying
    @Query("update Wallet w set w.balance=w.balance+:amount where w.contact = :contact")
    void updateWallet(String contact ,double amount);
}
