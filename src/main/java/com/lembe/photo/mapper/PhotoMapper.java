package com.lembe.photo.mapper;

import com.lembe.photo.domain.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PhotoMapper {

    void insert(Photo photo);

    Photo findById(Long photoSeq);

    List<Photo> findByUserSeq(@Param("userSeq") Long userSeq,
                              @Param("photoType") String photoType);  // null 이면 전체

    List<Photo> findMainByUserSeq(Long userSeq);

    void updateMain(@Param("userSeq") Long userSeq,
                    @Param("photoType") String photoType,
                    @Param("photoSeq") Long photoSeq);

    void softDelete(@Param("photoSeq") Long photoSeq,
                    @Param("userSeq") Long userSeq);
}
