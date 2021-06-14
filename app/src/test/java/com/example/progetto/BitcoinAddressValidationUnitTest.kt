package com.example.progetto

import com.example.progetto.models.NewTransactionViewModel
import com.example.progetto.repository.CoinsRepository
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BitcoinAddressValidationUnitTest {

    @Test
    fun check_validator() {
        assertTrue(addWalletFragment.Bitcoin.validateAddress("1KFzzGtDdnq5hrwxXGjwVnKzRbvf8WVxck"))
        assertTrue(addWalletFragment.Bitcoin.validateAddress("1GfmpY61wFck875DHNE8FMCrEmicx6pEy5"))
        assertTrue(addWalletFragment.Bitcoin.validateAddress("1PXdXFgkJGhW8jXpbntJBoS3ex1uy1UKus"))
        assertTrue(addWalletFragment.Bitcoin.validateAddress("19JXu9Q2F9PQ9W6E83oKuBGmiwGctHdLJj"))
        assertFalse(addWalletFragment.Bitcoin.validateAddress("19JXu9Q2F9PQ9W6E83oKuBGmiwGctHdLlj"))
        assertFalse(addWalletFragment.Bitcoin.validateAddress("18JXu9Q2F9PQ9W6E83oKuBGmiwGctHdLJj"))
        assertFalse(addWalletFragment.Bitcoin.validateAddress("bc1qr967m0w2p26ryn6xt39ctc09yrhfnwzmjsfg2w"))
        assertFalse(addWalletFragment.Bitcoin.validateAddress("168mFeWqcmHdmNFu9ku64Rm6MqTJP7MQF"))
    }
}