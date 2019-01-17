package com.stylefeng.guns;

import com.stylefeng.guns.rest.AlipayApplication;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlipayApplication.class)
public class GunsRestApplicationTests {

	@Autowired
	private FTPUtil ftpUtil;

	@Test
	public void contextLoads() {

		String fileStrByAddress = ftpUtil.getFileStrByAddress("seats/cgs.json");

		File filepath = new File("C:/Users/mayn/Desktop/qrcode/qr-6b61bc04-38b8-41b3-a600-71b1eeed504a.png");
		ftpUtil.uploadFile("qr-6b61bc04-38b8-41b3-a600-71b1eeed504a.png",filepath);
		System.out.println(fileStrByAddress);

	}

}