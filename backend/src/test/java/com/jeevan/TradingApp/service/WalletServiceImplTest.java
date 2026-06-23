package com.jeevan.TradingApp.service;

import com.jeevan.TradingApp.domain.LedgerTransactionType;
import com.jeevan.TradingApp.exception.InsufficientBalanceException;
import com.jeevan.TradingApp.modal.User;
import com.jeevan.TradingApp.modal.Wallet;
import com.jeevan.TradingApp.repository.UserRepository;
import com.jeevan.TradingApp.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private LedgerService ledgerService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private User sender;
    private User receiver;
    private Wallet senderWallet;
    private Wallet receiverWallet;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);
        sender.setEmail("sender@test.com");

        receiver = new User();
        receiver.setId(2L);
        receiver.setEmail("receiver@test.com");

        senderWallet = new Wallet();
        senderWallet.setId(10L);
        senderWallet.setUser(sender);
        senderWallet.setBalance(new BigDecimal("1000.00"));
        senderWallet.setLockedBalance(BigDecimal.ZERO);

        receiverWallet = new Wallet();
        receiverWallet.setId(11L);
        receiverWallet.setUser(receiver);
        receiverWallet.setBalance(new BigDecimal("500.00"));
        receiverWallet.setLockedBalance(BigDecimal.ZERO);
    }

    @Test
    void testWalletToWalletTransfer_Success() {
        // Mock getting sender's wallet
        when(walletRepository.findByUserId(sender.getId())).thenReturn(senderWallet);
        
        // Mock sufficient balance
        BigDecimal transferAmount = new BigDecimal("200.00");
        when(ledgerService.calculateAvailableBalance(sender.getId())).thenReturn(new BigDecimal("1000.00"));

        // Execute transfer
        Wallet result = walletService.walletToWalletTransfer(sender, receiverWallet, 200L);

        // Verify ledger entries
        verify(ledgerService).createLedgerEntry(
                eq(sender), eq(LedgerTransactionType.DEBIT), eq(transferAmount), isNull(), anyString());
        
        verify(ledgerService).createLedgerEntry(
                eq(receiver), eq(LedgerTransactionType.CREDIT), eq(transferAmount), isNull(), anyString());

        // Result should be the sender's wallet
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void testWalletToWalletTransfer_InsufficientBalance() {
        // Mock getting sender's wallet
        when(walletRepository.findByUserId(sender.getId())).thenReturn(senderWallet);

        // Mock insufficient balance
        when(ledgerService.calculateAvailableBalance(sender.getId())).thenReturn(new BigDecimal("50.00"));

        // Expect Exception
        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> walletService.walletToWalletTransfer(sender, receiverWallet, 200L)
        );

        assertTrue(exception.getMessage().contains("Wallet balance is insufficient"));

        // Verify NO ledger entries were created
        verify(ledgerService, never()).createLedgerEntry(any(), any(), any(), any(), anyString());
    }

    @Test
    void testAddBalance_Success() {
        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(walletRepository.findByUserId(sender.getId())).thenReturn(senderWallet);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        BigDecimal addAmount = new BigDecimal("300.00");
        Wallet result = walletService.addBalance(sender, addAmount);

        // Balance should be updated to 1300
        assertEquals(new BigDecimal("1300.00"), result.getBalance());
        
        verify(ledgerService).createLedgerEntry(
                eq(sender), eq(LedgerTransactionType.CREDIT), eq(addAmount), isNull(), anyString());
        verify(walletRepository).save(any(Wallet.class));
    }
}
