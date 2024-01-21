package com.qkwl.common.coin;

import com.qkwl.common.coin.driver.*;

import java.math.BigInteger;

/**
 * CoinDriverFactory
 * 
 * @author jany
 */
public class CoinDriverFactory {
	private final int coinType;
	private final String accessKey;
	private final String secretKey;
	private final String walletLink;
	private final String chainLink;
	private final String pass;
	private final BigInteger assetId;
	private final String sendAccount;
	private final String contractAccount;
	private final int contractWei;
	private final String shortName;
	private final String walletAccount;

	private CoinDriverFactory(Builder builder) {
		this.coinType = builder.coinType;
		this.accessKey = builder.accessKey;
		this.secretKey = builder.secretKey;
		this.walletLink = builder.walletLink;
		this.chainLink = builder.chainLink;
		this.pass = builder.pass;
		this.assetId = builder.assetId;
		this.sendAccount = builder.sendAccount;
		this.contractAccount = builder.contractAccount;
		this.contractWei = builder.contractWei;
		this.shortName = builder.shortName;
		this.walletAccount = builder.walletAccount;
	}

	/**
	 * 创建CoinDriver
	 */
	public CoinDriver getDriver() {
		switch (coinType) {
		// 对应 SystemCoinSortEnum 枚举类
		case 2: // BTC
			return new BTCDriver(accessKey, secretKey, walletLink,chainLink, pass, coinType,shortName);
		case 3: // ETH
		case 17: //vcc
			return new ETHDriver(walletLink,chainLink, pass, coinType, sendAccount, contractAccount, contractWei);
		case 5: // ETC
			return new ETCDriver(walletLink,chainLink, pass, coinType, sendAccount, contractAccount, contractWei);
		case 4: // ICS
		case 8: // MIC
			return new ICSDriver(accessKey, secretKey, walletLink,chainLink, pass, assetId, coinType);
		case 6: // ETP
			return new ETPDriver(accessKey, secretKey, walletLink,chainLink, pass, coinType, sendAccount);
		case 7: // GXB
			return new GXBDriver(coinType, accessKey, secretKey, walletLink,chainLink, sendAccount);
		case 9: // WICC
			return new WICCDriver(accessKey, secretKey, walletLink,chainLink, pass, coinType, sendAccount);
		case 10: // USDT
			return new USDTDriver(accessKey, secretKey, walletLink,chainLink, pass, coinType, sendAccount);
		case 11: //moac
			return new MOACDriver(walletLink,chainLink, pass, coinType, sendAccount, contractAccount, contractWei);
		case 12: // FOD
			return new FODDriver(walletLink,chainLink, pass, coinType, sendAccount, contractAccount, contractWei);
		case 13: // EOS
			return new EOSDriver(accessKey, secretKey, walletLink, chainLink, pass, coinType, sendAccount,contractAccount,shortName,walletAccount);
		case 14: // bwt
			return new BWTDriver(accessKey, secretKey, walletLink, chainLink, pass, coinType, sendAccount,contractAccount,shortName,walletAccount);
		case 15: // ruby
			return new RUBYDriver(accessKey, secretKey, walletLink,chainLink, pass, coinType);
		case 16: // neo
			return new NEODriver(walletLink,chainLink, pass, coinType, sendAccount, contractAccount, contractWei,shortName);
		case 18: // bts类
			return new BTSDriver(walletLink,chainLink, pass, coinType, sendAccount, contractAccount, contractWei,shortName,walletAccount);
		case 19: // MULTI类
			return new MULTIDriver(accessKey, secretKey, walletLink,chainLink, pass, coinType,shortName,sendAccount,contractAccount);
		case 20: // chain33类
			return new Chain33Driver(walletLink, chainLink, pass, coinType, sendAccount, contractAccount, shortName, contractWei);
		case 25: 
			return new TCPDriver(accessKey, secretKey, walletLink, chainLink, pass, coinType, sendAccount,contractAccount,shortName,walletAccount);
		default:
			return null;
		}
	}

	public static class Builder {
		private int coinType;
		private String accessKey;
		private String secretKey;
		private String walletLink;
		private String chainLink;
		private String pass;
		private BigInteger assetId;
		private String sendAccount;
		private String contractAccount;
		private int contractWei;
		private String shortName;
		private String walletAccount;

		public Builder(int coinType, String walletLink, String chainLink) {
			this.coinType = coinType;
			this.walletLink = walletLink;
			this.chainLink = chainLink;
		}

		public Builder accessKey(String accessKey) {
			this.accessKey = accessKey;
			return this;
		}

		public Builder secretKey(String secretKey) {
			this.secretKey = secretKey;
			return this;
		}

		public Builder pass(String pass) {
			this.pass = pass;
			return this;
		}

		public Builder assetId(BigInteger assetId) {
			this.assetId = assetId;
			return this;
		}

		public Builder sendAccount(String sendAccount) {
			this.sendAccount = sendAccount;
			return this;
		}

		public Builder contractAccount(String contractAccount) {
			this.contractAccount = contractAccount;
			return this;
		}

		public Builder shortName(String shortName) {
			this.shortName = shortName;
			return this;
		}
		
		public Builder contractWei(int contractWei) {
			this.contractWei = contractWei;
			return this;
		}
		
		public Builder walletAccount(String walletAccount) {
			this.walletAccount = walletAccount;
			return this;
		}

		public CoinDriverFactory builder() {
			return new CoinDriverFactory(this);
		}
	}
}
