package com.conan.crawler.server.pre.mapper;

import com.conan.crawler.server.pre.entity.KeyWordScanTb;

public interface KeyWordScanTbMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table key_word_scan_tb
     *
     * @mbg.generated Thu Apr 05 10:55:30 CST 2018
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table key_word_scan_tb
     *
     * @mbg.generated Thu Apr 05 10:55:30 CST 2018
     */
    int insert(KeyWordScanTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table key_word_scan_tb
     *
     * @mbg.generated Thu Apr 05 10:55:30 CST 2018
     */
    int insertSelective(KeyWordScanTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table key_word_scan_tb
     *
     * @mbg.generated Thu Apr 05 10:55:30 CST 2018
     */
    KeyWordScanTb selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table key_word_scan_tb
     *
     * @mbg.generated Thu Apr 05 10:55:30 CST 2018
     */
    int updateByPrimaryKeySelective(KeyWordScanTb record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table key_word_scan_tb
     *
     * @mbg.generated Thu Apr 05 10:55:30 CST 2018
     */
    int updateByPrimaryKey(KeyWordScanTb record);
}