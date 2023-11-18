package com.spring.board.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.spring.board.service.BoardService;
import com.spring.board.vo.ArticleVO;
import com.spring.board.vo.ImageVO;
import com.spring.member.vo.MemberVO;

@Controller("boardController")
public class BoardControllerImpl implements BoardController {
	private static Logger logger = LoggerFactory.getLogger(BoardController.class);
	private static final String ARTICLE_IMAGE_REPO = "/home/cbpark68/file_repo/bsleepro30";
	@Autowired
	private BoardService boardService;
	@Autowired
	private ArticleVO articleVO;

	@Override
	@RequestMapping(value = "/board/listArticles.do", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView listArticles(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String viewName = (String) request.getAttribute("viewName");
		List list = boardService.listArticles();
		ModelAndView mav = new ModelAndView(viewName);
		mav.addObject("articlesList", list);
		return mav;
	}

//	@Override
//	@RequestMapping(value = "/board/addNewArticle.do", method = RequestMethod.POST)
//	@ResponseBody
//	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
//			throws Exception {
//		multipartRequest.setCharacterEncoding("utf-8");
//		Map<String, Object> map = new HashMap<String, Object>();
//		Enumeration enu = multipartRequest.getParameterNames();
//		while (enu.hasMoreElements()) {
//			String name = (String) enu.nextElement();
//			String value = multipartRequest.getParameter(name);
//			map.put(name, value);
//		}
//		String imageFileName = upload(multipartRequest);
//		HttpSession session = multipartRequest.getSession();
//		MemberVO vo = (MemberVO) session.getAttribute("member");
//		String id = vo.getId();
//		map.put("parentNO", 0);
//		map.put("id", id);
//		map.put("imageFileName", imageFileName);
//		String message;
//		ResponseEntity resEnt = null;
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "text/html;charset=utf-8");
//		try {
//			int articleNO = boardService.addNewArticle(map);
//			if (imageFileName != null && imageFileName.length() != 0) {
//				File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
//				File destDir = new File(ARTICLE_IMAGE_REPO + "/" + articleNO);
//				FileUtils.moveFileToDirectory(srcFile, destDir, true);
//			}
//			message = "<script>";
//			message += " alert('새글이 추가되었습니다.');";
//			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
//			message += " </script> ";
//			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
//		} catch (Exception e) {
//			File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
//			srcFile.delete();
//			message = "<script>";
//			message += " alert('오류가 발생했습니다. 다시 시도해 주세요.');";
//			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
//			message += " </script> ";
//			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
//			e.printStackTrace();
//		}
//		return resEnt;
//	}
	
	@Override
	@RequestMapping(value = "/board/addNewArticle.do", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity addNewArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		multipartRequest.setCharacterEncoding("utf-8");
		String imageFileName = null;
		Map map = new HashMap();
		Enumeration enu = multipartRequest.getParameterNames();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			String value = multipartRequest.getParameter(name);
			map.put(name, value);
		}
		HttpSession session = multipartRequest.getSession();
		MemberVO vo = (MemberVO) session.getAttribute("member");
		String id = vo.getId();
		map.put("parentNO", 0);
		map.put("id", id);

		List<String> fileList = upload(multipartRequest);
		List<ImageVO> imageFileList = new ArrayList<ImageVO>();
		if(fileList != null && fileList.size() != 0) {
			for(String fileName:fileList) {
				ImageVO ivo = new ImageVO();
				ivo.setImageFileName(fileName);
				imageFileList.add(ivo);
			}
			map.put("imageFileList", imageFileList);
		}
		String message;
		ResponseEntity resEnt = null;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html;charset=utf-8");
		try {
			int articleNO = boardService.addNewArticle(map);
			if (imageFileList != null && imageFileList.size() != 0) {
				for(ImageVO ivo:imageFileList) {
					imageFileName = ivo.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "/" + articleNO);
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
			}
			message = "<script>";
			message += " alert('새글이 추가되었습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/listArticles.do'; ";
			message += " </script> ";
			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
		} catch (Exception e) {
			if (imageFileName != null && imageFileName.length() != 0) {
				for(ImageVO ivo:imageFileList) {
					imageFileName = ivo.getImageFileName();
					File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
					srcFile.delete();
				}
			}
			File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
			srcFile.delete();
			message = "<script>";
			message += " alert('오류가 발생했습니다. 다시 시도해 주세요.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/articleForm.do'; ";
			message += " </script> ";
			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

//	@Override
//	@RequestMapping(value="/board/viewArticle.do",method=RequestMethod.GET)
//	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request, HttpServletResponse response)
//			throws Exception {
//		String viewName = (String)request.getAttribute("viewName");
//		articleVO = boardService.viewArticle(articleNO);
//		ModelAndView mav = new ModelAndView();
//		mav.setViewName(viewName);
//		mav.addObject("article",articleVO);
//		return mav;
//	}

	@Override
	@RequestMapping(value="/board/viewArticle.do",method=RequestMethod.GET)
	public ModelAndView viewArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String viewName = (String)request.getAttribute("viewName");
		Map map = boardService.viewArticle(articleNO);
		ModelAndView mav = new ModelAndView();
		mav.setViewName(viewName);
		mav.addObject("articleMap",map);
		return mav;
	}

	@Override
	@RequestMapping(value="/board/modArticle.do",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity modArticle(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
			throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		Enumeration enu =  multipartRequest.getParameterNames();
		while(enu.hasMoreElements()) {
			String name = (String)enu.nextElement();
			String value = multipartRequest.getParameter(name);
			map.put(name, value);
		}
		String imageFileName = upload(multipartRequest);
		map.put("imageFileName", imageFileName);
		String articleNO = (String)map.get("articleNO");
		String message = null;
		ResponseEntity resEnt = null;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html;charset=utf-8");
		try {
			boardService.modArticle(map);
			if(imageFileName != null && imageFileName.length() != 0) {
				File srcFile = new File(ARTICLE_IMAGE_REPO+"/temp/"+imageFileName);
				File destFile = new File(ARTICLE_IMAGE_REPO+"/"+articleNO);
				FileUtils.moveFileToDirectory(srcFile, destFile, true);
				String originalFileName = (String)map.get("originalFileName");
				File oldFile = new File(ARTICLE_IMAGE_REPO+"/"+articleNO+"/"+originalFileName);
				oldFile.delete();
			}
			message = "<script>";
			message += " alert('글을 수정했습니다.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/viewArticle.do?articleNO="+articleNO+"'; ";
			message += " </script> ";
			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
		}catch(Exception e) {
			File srcFile = new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName);
			srcFile.delete();
			message = "<script>";
			message += " alert('오류가 발생했습니다. 다시 시도해 주세요.');";
			message += " location.href='" + multipartRequest.getContextPath() + "/board/viewArticle.do?articleNO="+articleNO+"'; ";
			message += " </script> ";
			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

	@Override
	@RequestMapping(value="/board/removeArticle.do",method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity removeArticle(@RequestParam("articleNO") int articleNO, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String message = "";
		ResponseEntity resEnt = null;
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html;charset=utf-8");
		try {
			boardService.removeArticle(articleNO);
			File destDir = new File(ARTICLE_IMAGE_REPO+"/"+articleNO);
			FileUtils.deleteDirectory(destDir);
			message = "<script>";
			message += " alert('글을 삭제했습니다.');";
			message += " location.href='" + request.getContextPath() + "/board/listArticles.do'; ";
			message += " </script> ";
			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
		} catch (Exception e) {
			message = "<script>";
			message += " alert('오류가 발생했습니다. 다시 시도해 주세요.');";
			message += " location.href='" + request.getContextPath() + "/board/listArticles.do'; ";
			message += " </script> ";
			resEnt = new ResponseEntity(message, headers, HttpStatus.CREATED);
			e.printStackTrace();
		}
		return resEnt;
	}

//	private String upload(MultipartHttpServletRequest multipartRequest) throws Exception {
//		String imageFileName = null;
//		Iterator<String> fileNames = multipartRequest.getFileNames();
//		while (fileNames.hasNext()) {
//			String fileName = fileNames.next();
//			MultipartFile mFile = multipartRequest.getFile(fileName);
//			imageFileName = mFile.getOriginalFilename();
//			File file = new File(ARTICLE_IMAGE_REPO + "/temp/" + fileName);
//			if (mFile.getSize() != 0) {
//				if (!file.exists()) {
//					file.getParentFile().mkdirs();
//					mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "/temp/" + imageFileName));
//				}
//			}
//		}
//		return imageFileName;
//	}
	
	private List<String> upload(MultipartHttpServletRequest multipartRequest) throws Exception {
		List<String> fileList = new ArrayList<String>();
		Iterator<String> fileNames = multipartRequest.getFileNames();
		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			MultipartFile mFile = multipartRequest.getFile(fileName);
			String originalFileName = mFile.getOriginalFilename();
			fileList.add(originalFileName);
			File file = new File(ARTICLE_IMAGE_REPO + "/temp/" + fileName);
			if (mFile.getSize() != 0) {
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					mFile.transferTo(new File(ARTICLE_IMAGE_REPO + "/temp/" + originalFileName));
				}
			}
		}
		return fileList;
	}
	
	@RequestMapping(value="/board/*Form.do",method=RequestMethod.GET)
	private ModelAndView form(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String viewName = (String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		return mav;
	}
}
