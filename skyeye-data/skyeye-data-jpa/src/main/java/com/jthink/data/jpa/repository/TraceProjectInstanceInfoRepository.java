package com.jthink.skyeye.data.jpa.repository;

import com.jthink.skyeye.data.jpa.domain.TraceProjectInstanceInfo;
import com.jthink.skyeye.data.jpa.dto.TraceProjectInstanceDto;
import com.jthink.skyeye.data.jpa.pk.TraceProjectInstanceInfoPK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-11-22 13:59:31
 */
public interface TraceProjectInstanceInfoRepository
        extends CrudRepository<TraceProjectInstanceInfo, TraceProjectInstanceInfoPK> {

    @Query(value = "select new com.jthink.skyeye.data.jpa.dto.TraceProjectInstanceDto" +
            "(a.hostName,a.primary.hostMac,a.primary.projectName,a.primary.instancePath," +
            "a.hostId,a.projectId,a.instanceId,a.remark) " +
            "from TraceProjectInstanceInfo a " +
            "where a.primary.hostMac=?1 and a.primary.projectName=?2 order by a.instanceId desc")
    public List<TraceProjectInstanceDto> findByMacAndProject(String hostMac, String projectName);
}
