package com.ruoyi.news.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.news.domain.model.ArticleEO;
import com.ruoyi.news.mapper.es.ArticleEOMapper;
import com.ruoyi.news.util.enums.*;
import com.ruoyi.news.util.UuidUtil;
import com.ruoyi.news.util.result.ResponseStatusEnum;
import com.ruoyi.system.api.domain.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.news.mapper.ChuArticleMapper;
import com.ruoyi.news.domain.ChuArticle;
import com.ruoyi.news.service.IChuArticleService;

/**
 * 文章资讯Service业务层处理
 * 
 * @author  xiaowu
 * @date 2022-10-18
 */
@Service
public class ChuArticleServiceImpl implements IChuArticleService 
{
    @Autowired
    private ChuArticleMapper chuArticleMapper;

    @Autowired
    private ArticleEOMapper articleEOMapper;
    /**
     * 查询文章资讯
     * 
     * @param id 文章资讯主键
     * @return 文章资讯
     */
    @Override
    public ChuArticle selectChuArticleById(String id)
    {
        return chuArticleMapper.selectChuArticleById(id);
    }

    /**
     * 查询文章资讯列表
     * 
     * @param chuArticle 文章资讯
     * @return 文章资讯
     */
    @Override
    public List<ChuArticle> selectChuArticleList(ChuArticle chuArticle)
    {
        return chuArticleMapper.selectChuArticleList(chuArticle);
    }

    /**
     * 新增文章资讯
     * 
     * @param chuArticle 文章资讯
     * @return 结果
     */
    @Override
    public int insertChuArticle(ChuArticle chuArticle)
    {
        SysUser sysUser = SecurityUtils.getLoginUser().getSysUser();
        chuArticle.setId(UuidUtil.getShortUuid());
        chuArticle.setCreateTime(DateUtils.getNowDate());
        chuArticle.setArticleType(1);
        chuArticle.setIsAppoint(0);
        chuArticle.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        chuArticle.setCommentCounts(0L);
        chuArticle.setReadCounts(0L);
        chuArticle.setIsDelete(YesOrNo.NO.type);
        chuArticle.setUpdateTime(new Date());
        chuArticle.setUpdateTime(new Date());
        chuArticle.setPublishUserId(sysUser.getUserId()+"");
        chuArticle.setPublishTime(new Date());

        //同时将数据新增到es中
        ArticleEO articleEO = new ArticleEO();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BeanUtils.copyProperties(chuArticle, articleEO);
        articleEO.setPublishTime( simpleDateFormat.format(chuArticle.getPublishTime()));
        articleEO.setId(chuArticle.getId());
        int success = articleEOMapper.insert(articleEO);

        return chuArticleMapper.insertChuArticle(chuArticle);
    }

    /**
     * 修改文章资讯
     * 
     * @param chuArticle 文章资讯
     * @return 结果
     */
    @Override
    public int updateChuArticle(ChuArticle chuArticle)
    {
        chuArticle.setUpdateTime(DateUtils.getNowDate());
        //同时修改es中的数据
        ArticleEO articleEO = new ArticleEO();
        BeanUtils.copyProperties(chuArticle, articleEO);
        articleEO.setId(chuArticle.getId());
        int count = articleEOMapper.updateById(articleEO);

        return chuArticleMapper.updateChuArticle(chuArticle);
    }

    /**
     * 批量删除文章资讯
     * 
     * @param ids 需要删除的文章资讯主键
     * @return 结果
     */
    @Override
    public int deleteChuArticleByIds(String[] ids)
    {
        //同时删除es中的数据
        List<String> idList = Arrays.asList(ids);
        int count = articleEOMapper.deleteBatchIds(idList);

        return chuArticleMapper.deleteChuArticleByIds(ids);
    }

    /**
     * 删除文章资讯信息
     * 
     * @param id 文章资讯主键
     * @return 结果
     */
    @Override
    public int deleteChuArticleById(String id)
    {
        return chuArticleMapper.deleteChuArticleById(id);
    }
}
