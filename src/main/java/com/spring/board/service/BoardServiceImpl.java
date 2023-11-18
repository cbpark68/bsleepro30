package com.spring.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring.board.dao.BoardDAO;
import com.spring.board.vo.ArticleVO;
import com.spring.board.vo.ImageVO;

@Service("boardService")
@Transactional(propagation = Propagation.REQUIRED)
public class BoardServiceImpl implements BoardService {
	@Autowired
	private BoardDAO boardDAO;
	@Autowired
	private ArticleVO articleVO;

	@Override
	public List<ArticleVO> listArticles() throws Exception {
		List<ArticleVO> list = boardDAO.selectAllArticlesList();
		return list;
	}

//	@Override
//	public int addNewArticle(Map articleMap) throws Exception {
//		return boardDAO.insertNewArticle(articleMap);
//	}

	@Override
	public int addNewArticle(Map articleMap) throws Exception {
		int articleNO = boardDAO.insertNewArticle(articleMap);
		articleMap.put("articleNO", articleNO);
		boardDAO.insertNewImage(articleMap);
		return articleNO;
	}

//	@Override
//	public ArticleVO viewArticle(int articleNO) throws Exception {
//		ArticleVO vo = boardDAO.selectArticle(articleNO);
//		return vo;
//	}

	@Override
	public void modArticle(Map articleMap) throws Exception {
		boardDAO.updateArticle(articleMap);
	}

	@Override
	public void removeArticle(int articleNO) throws Exception {
		boardDAO.deleteArticle(articleNO);
	}

	@Override
	public Map viewArticle(int articleNO) throws Exception {
		Map map = new HashMap();
		ArticleVO avo = boardDAO.selectArticle(articleNO);
		List<ImageVO> imageFileList = boardDAO.selectImageFileList(articleNO);
		map.put("article", avo);
		map.put("imageFileList", imageFileList);
		return map;
	}

}
