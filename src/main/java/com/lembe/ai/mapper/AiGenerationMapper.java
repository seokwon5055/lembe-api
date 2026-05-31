package com.lembe.ai.mapper;

import com.lembe.ai.domain.AiGenerationJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AiGenerationMapper {

    void insert(AiGenerationJob job);

    AiGenerationJob findById(Long genSeq);

    List<AiGenerationJob> findByUcode(String ucode);

    AiGenerationJob findLatestByMilestoneSeq(Long milestoneSeq);

    void updateCompleted(@Param("genSeq") Long genSeq,
                         @Param("resultPhotoSeq") Long resultPhotoSeq,
                         @Param("falRequestId") String falRequestId,
                         @Param("costUsd") BigDecimal costUsd);

    void updateFailed(@Param("genSeq") Long genSeq,
                      @Param("errorMsg") String errorMsg);

    int countTodayByUserSeq(String ucode);

    BigDecimal sumTodayCostUsd();

    boolean existsFreeRetry(@Param("ucode") String ucode,
                            @Param("milestoneSeq") Long milestoneSeq);
}
