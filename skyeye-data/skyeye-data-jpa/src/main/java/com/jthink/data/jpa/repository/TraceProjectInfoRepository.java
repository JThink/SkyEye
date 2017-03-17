package com.jthink.skyeye.data.jpa.repository;

import com.jthink.skyeye.data.jpa.domain.TraceProjectInfo;
import com.jthink.skyeye.data.jpa.dto.TraceProjectInfoDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-10-09 09:57:28
 */
public interface TraceProjectInfoRepository extends CrudRepository<TraceProjectInfo, String> {

    /**
     * 根据sql查询
     *
     * @return
     */
    @Query(value = "select new com.jthink.skyeye.data.jpa.dto.TraceProjectInfoDto" +
            "(a.projectName, a.projectId, a.remark) from TraceProjectInfo a where a.projectName=?1")
    public List<TraceProjectInfoDto> findByProjectName(String projectName);

    @Query(value = "select new com.jthink.skyeye.data.jpa.dto.TraceProjectInfoDto" +
            "(a.projectName, a.projectId, a.remark) from TraceProjectInfo a where a.projectId=?1")
    public List<TraceProjectInfoDto> findByProjectId(Integer projectId);

}
