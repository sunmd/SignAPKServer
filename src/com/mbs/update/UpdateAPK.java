package com.mbs.update;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	@SuppressWarnings({ "null" })
	public void doPost(HttpServletRequest request, HttpServletResponse response) {


		// 创建文件项目工厂对象
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// 设置文件上传路径
		String upload = this.getServletContext().getRealPath("/upload");
		
		// 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
		String temp = System.getProperty("java.io.tmpdir");
		
		// 设置缓冲区大小为 5M
		factory.setSizeThreshold(1024 * 1024 * 5);
		
		// 设置临时文件夹为temp
		factory.setRepository(new File(temp));
		
		// 用工厂实例化上传组件,ServletFileUpload 用来解析文件上传请求
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
		
		//设置上传文件大小
		servletFileUpload.setFileSizeMax(1024*1024*100);
		
		//设置单个文件大小
		servletFileUpload.setSizeMax(1024*1024*100);
				
		//输入apk的名称
		String importAPK = "demo.apk";
		
		//request的输入流
		InputStream is = null;
		
		// 解析结果放在List中
		try
		{
			//解析form的请求
			@SuppressWarnings("unchecked")
			List<FileItem> list = servletFileUpload.parseRequest(request);
			
			for (FileItem item : list)
			{
				String name = item.getFieldName();
				
				try {
					is = item.getInputStream();
					
					if (name.contains("content"))
					{
						//如果是文本进行输出
						System.out.println("content:" + inputStream2String(is));
						
					} else if(name.contains("file"))
					{
						try
						{
							System.out.println("sunmd--- upload:" + upload);
							System.out.println("sunmd--- getName:" + item.getName());
							importAPK = item.getName();
							//读取输入流，生成文件然后签名
							inputStream2File(is, upload + File.separator + item.getName());
							
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					
				} finally {
					
					try {
						is.close();
					} catch (IOException e) {
						
						e.printStackTrace();
					} finally {
						is = null;
					}
					
				}
			}
			
			
			//platform.x509.pem  platform.pk8 %%~ni.apk %%~ni_sign.apk
			String[] args = new String[4];
			
			args[0] = upload + File.separator + "platform.x509.pem";
			args[1] = upload + File.separator + "platform.pk8";
			args[2] = upload + File.separator + importAPK;
			
			System.out.println("sunmd --- importAPK = " + importAPK);
			String exportAPK = importAPK.substring(0, importAPK.lastIndexOf(".")) + "_signed.apk";
			System.out.println("sunmd --- exportAPK = " + exportAPK);
			
			//调整编码uft-8
			try {
				exportAPK = URLEncoder.encode(exportAPK, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			args[3] = upload + exportAPK;
			SignAPK.sign(args);
			File f = new File(args[3]);  
			
			 if(f.exists()){  
				 //文件输入流
				 FileInputStream  fis = null;
				 //文件名字
				 String filename = "sign.apk";
				 
				//获取响应报文输出流对象  
		        ServletOutputStream  servletOutputStream = null; 
				 
				 try {
					 fis = new FileInputStream(f);  
					 filename = URLEncoder.encode(f.getName(),"utf-8"); //解决中文文件名下载后乱码的问题
					 byte[] b = new byte[fis.available()];  
			         fis.read(b);
			         
			         
			         response.setCharacterEncoding("utf-8");  
			            
			         response.setHeader("Content-Disposition","attachment; filename="+filename+"");
			         //输出  
			         servletOutputStream =response.getOutputStream();
		             servletOutputStream.write(b);  
		             servletOutputStream.flush();  
		             
				 } catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						fis.close();
						servletOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						fis = null;
						servletOutputStream = null;
					}
					
					
				}
		            
		            
		             
		             
		             
		        }  
			
		} catch (FileUploadException e)
		{
			
			e.printStackTrace();
			response.setContentType("text/html");
			PrintWriter out = null;
			try {
				out = response.getWriter();
				out.write("failure");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				out.close();	
			}
			
			
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
		public static void inputStream2File(InputStream is, String savePath) {
			
			System.out.println("文件保存路径为:" + savePath);
			File file = new File(savePath);
			BufferedInputStream fis = new BufferedInputStream(is);
			FileOutputStream fos = null;
			try {
				int f;
				fos = new FileOutputStream(file);
				
				while ((f = fis.read()) != -1)
				{
					fos.write(f);
				}
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					fos.close();
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					fos = null;
					fis = null;
				}
				
			}
			

		}
		
}  