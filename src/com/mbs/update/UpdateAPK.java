package com.mbs.update;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UpdateAPK extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor of the object.
	 */
	public UpdateAPK() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);//get和post用相同操作先
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		

		// 创建文件项目工厂对象
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// 设置文件上传路径
		String upload = this.getServletContext().getRealPath("/upload/");
		// 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
		String temp = System.getProperty("java.io.tmpdir");
		// 设置缓冲区大小为 5M
		factory.setSizeThreshold(1024 * 1024 * 100);
		// 设置临时文件夹为temp
		factory.setRepository(new File(temp));
		// 用工厂实例化上传组件,ServletFileUpload 用来解析文件上传请求
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		
		//输入apk的名称
		String importAPK = "demo.apk";
		// 解析结果放在List中
		try
		{
			List<FileItem> list = servletFileUpload.parseRequest(request);

			for (FileItem item : list)
			{
				String name = item.getFieldName();
				InputStream is = item.getInputStream();

				if (name.contains("content"))
				{
					System.out.println(inputStream2String(is));
				} else if(name.contains("file"))
				{
					try
					{
						System.out.println("sunmd--- upload:" + upload);
						System.out.println("sunmd--- getName:" + item.getName());
						importAPK = item.getName();
						inputStream2File(is, upload + "\\" + item.getName());
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				is.close();
			}
			
			
			//platform.x509.pem  platform.pk8 %%~ni.apk %%~ni_sign.apk
			String[] args = new String[4];
			args[0] = upload + "\\" + "platform.x509.pem";
			args[1] = upload + "\\" + "platform.pk8";
			args[2] = upload + "\\" + importAPK;
			System.out.println("sunmd --- importAPK = " + importAPK);
			String exportAPK = importAPK.substring(0, importAPK.lastIndexOf(".")) + "_signed.apk";
			System.out.println("sunmd --- exportAPK = " + exportAPK);
			//调整编码uft-8
			exportAPK = URLEncoder.encode(exportAPK, "UTF-8");
			args[3] = upload + exportAPK;
			SignAPK.sign(args);
			File f = new File(args[3]);  
			 if(f.exists()){  
		            FileInputStream  fis = new FileInputStream(f);  
		            String filename=URLEncoder.encode(f.getName(),"utf-8"); //解决中文文件名下载后乱码的问题  
		            byte[] b = new byte[fis.available()];  
		            fis.read(b);  
		            response.setCharacterEncoding("utf-8");  
		            response.setHeader("Content-Disposition","attachment; filename="+filename+"");  
		            //获取响应报文输出流对象  
		            ServletOutputStream  servletOutputStream =response.getOutputStream();  
		            //输出  
		            servletOutputStream.write(b);  
		            servletOutputStream.flush();  
		            servletOutputStream.close();  
		        }  
			
		} catch (FileUploadException e)
		{
			
			e.printStackTrace();
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.write("failure");
			out.close();
		}
		
		
	}
	
	// 流转化成字符串
		public static String inputStream2String(InputStream is) throws IOException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = is.read()) != -1)
			{
				baos.write(i);
			}
			return baos.toString();
		}

		// 流转化成文件
		public static void inputStream2File(InputStream is, String savePath)
				throws Exception
		{
			System.out.println("文件保存路径为:" + savePath);
			File file = new File(savePath);
			InputStream inputSteam = is;
			BufferedInputStream fis = new BufferedInputStream(inputSteam);
			FileOutputStream fos = new FileOutputStream(file);
			int f;
			while ((f = fis.read()) != -1)
			{
				fos.write(f);
			}
			fos.flush();
			fos.close();
			fis.close();
			inputSteam.close();
			
		}
		
		
	    }  