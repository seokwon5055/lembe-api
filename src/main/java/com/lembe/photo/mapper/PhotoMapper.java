package com.lembe.photo.mapper;

import com.lembe.photo.domain.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PhotoMapper {

    void insert(Photo photo);

    Photo findById(Long photoSeq);

    List<Photo> findByUcode(@Param("ucode") String ucode,
                              @Param("photoType") String photoType);  // null 이면 전체

    List<Photo> findMainByUserSeq(String ucode);

    void updateMain(@Param("ucode") String ucode,
                    @Param("photoType") String photoType,
                    @Param("photoSeq") Long photoSeq);

    void softDelete(@Param("photoSeq") Long photoSeq,
                    @Param("ucode") String ucode);
}
