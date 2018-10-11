package com.hxd.jewelry.simple.utils;

import android.nfc.tech.NfcV;

import java.io.IOException;

//找到的暂时看来比较靠谱的NFCV操作集成 下面的注释都为摘�?

/**
 * * 用法  * NfcV mNfcV = NfcV.get(tag);   * mNfcV.connect();  * NfcVUtil
 * mNfcVutil = new NfcVUtil(mNfcV);  * 取得UID  * mNfcVutil.getUID();  *
 * 读取block�?1位置的内�?  * mNfcVutil.readOneBlock(1);  * 从位�?7�?始读2个block的内�? *
 * mNfcVutil.readBlocks(7, 2);   * 取得block的个�?  * mNfcVutil.getBlockNumber();  *
 * 取得1个block的长�?  * mNfcVutil.getOneBlockSize();  * �?位置1的block写内�?  *
 * mNfcVutil.writeBlock(1, new byte[]{0, 0, 0, 0})  
 */
public class NfcVUtil {
	private NfcV mNfcV;
	/** UID数组行式 */
	private byte[] ID;
	private String UID;
	private String UID_MT;
	private String DSFID;
	private String AFI;
	/** block的个�? */
	private int blockNumber;
	/** �?个block长度 */
	private int oneBlockSize;
	/** 信息 */
	private byte[] infoRmation;

	/**
	 *  * 初始�?  * @param mNfcV NfcV对象  * @throws IOException  
	 */
	public NfcVUtil(NfcV mNfcV) throws IOException {
		this.mNfcV = mNfcV;
		ID = this.mNfcV.getTag().getId();
		byte[] uid = new byte[ID.length];
		int j = 0;
		for (int i = 0; i < ID.length; i++) {
			uid[j] = ID[i];
			j++;
		}
		this.UID = printHexString(uid);
		byte[] uid_mt = new byte[ID.length];
		j = 0;
		for (int i = ID.length - 1; i >= 0; i--) {
			uid_mt[j] = ID[i];
			j++;
		}
		this.UID_MT = printHexString(uid_mt);
		getInfoRmation();
	}

	public String getUID() {
		return UID;
	}
	public String getUID_MT() {
		return UID_MT;
	}

	/**
	 *  * 取得标签信息   
	 */
	private byte[] getInfoRmation() throws IOException {
		byte[] cmd = new byte[10];
		cmd[0] = (byte) 0x22; // flag
		cmd[1] = (byte) 0x2B; // command
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		infoRmation = mNfcV.transceive(cmd);
		blockNumber = infoRmation[12];
		oneBlockSize = infoRmation[13];
		AFI = printHexString(new byte[] { infoRmation[11] });
		DSFID = printHexString(new byte[] { infoRmation[10] });
		return infoRmation;
	}

	public String getDSFID() {
		return DSFID;
	}

	public String getAFI() {
		return AFI;
	}

	public int getBlockNumber() {
		return blockNumber + 1;
	}

	public int getOneBlockSize() {
		return oneBlockSize + 1;
	}

	/**
	 *  * 读取�?个位置在position的block  * @param position 要读取的block位置  * @return
	 * 返回内容字符�?  * @throws IOException  
	 */
	public String readOneBlock(int position) throws IOException {
		byte cmd[] = new byte[11];
		cmd[0] = (byte) 0x22;
		cmd[1] = (byte) 0x20;
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		cmd[10] = (byte) position;
		byte res[] = mNfcV.transceive(cmd);
		if (res[0] == 0x00) {
			byte block[] = new byte[res.length - 1];
			System.arraycopy(res, 1, block, 0, res.length - 1);
			String temp = new String(block, "utf-8");
			return temp;// printHexString(block);
		}
		return null;
	}

	public String readOneBlock2(int position) throws IOException {
		byte cmd[] = new byte[11];
		cmd[0] = (byte) 0x22;
		cmd[1] = (byte) 0x20;
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		cmd[10] = (byte) position;
		byte res[] = mNfcV.transceive(cmd);
		if (res[0] == 0x00) {
			byte block[] = new byte[res.length - 1];
			System.arraycopy(res, 1, block, 0, res.length - 1);
			return printHexString(block);
		}
		return null;
	}

	/**
	 *  * 读取从begin�?始end个block  * begin + count 不能超过blockNumber  * @param begin
	 * block�?始位�?  * @param count 读取block数量  * @return 返回内容字符�?  * @throws
	 * IOException  
	 */
	public String readBlocks(int begin, int count) throws IOException {
		if ((begin + count) > blockNumber) {
			count = blockNumber - begin;
		}
		StringBuffer data = new StringBuffer();
		for (int i = begin; i <= count + begin; i++) {
			String temp = readOneBlock(i);
			if (temp == null)
				continue;
			data.append(readOneBlock(i));
		}
		return data.toString();
	}

	public String readBlocks2(int begin, int count) throws IOException {
		if ((begin + count) > blockNumber) {
			count = blockNumber - begin;
		}
		StringBuffer data = new StringBuffer();
		for (int i = begin; i <= count + begin; i++) {
			data.append(readOneBlock2(i));
		}
		return data.toString();
	}

	/**
	 *  * 将byte[]转换�?16进制字符�?  * @param data 要转换成字符串的字节数组  * @return 16进制字符�?  
	 */
	public static String printHexString(byte[] data) {
		StringBuffer s = new StringBuffer();
		;
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(data[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			s.append(hex);
		}
		return s.toString();
	}

	/**
	 *  * 将数据写入到block,  * @param position 要写内容的block位置  * @param data
	 * 要写的内�?,必须长度为blockOneSize  * @return false为写入失败，true为写入成�?  * @throws
	 * IOException   
	 */
	public boolean writeBlock(int position, byte[] data) throws IOException {
		byte cmd[] = new byte[15];
		cmd[0] = (byte) 0x22;
		cmd[1] = (byte) 0x21;
		System.arraycopy(ID, 0, cmd, 2, ID.length); // UID
		// block
		cmd[10] = (byte) 0x02;
		// value
		System.arraycopy(data, 0, cmd, 11, data.length);
		byte[] rsp = mNfcV.transceive(cmd);
		if (rsp[0] == 0x00)
			return true;
		return false;
	}

	/**
	 * 从编号为startblock的扇区开始一次写五块扇区每个扇区�?4个byte �?以data的长度是4byte*5=20byte
	 * */
	public boolean writefiveBlock(int startblock, byte[] data)
			throws IOException {
		for (int blocknum = startblock; blocknum < startblock + 5; blocknum++) {
			byte tmp[] = new byte[4];
			System.arraycopy(data, (blocknum - startblock) * 4, tmp, 0, 4);
			if (!writeBlock(blocknum, tmp))
				return false;
		}
		return true;
	}
}