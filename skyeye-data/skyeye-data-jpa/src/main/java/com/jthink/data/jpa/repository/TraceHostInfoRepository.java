package com.jthink.skyeye.data.jpa.repository;

import com.jthink.skyeye.data.jpa.domain.TraceHostInfo;
import com.jthink.skyeye.data.jpa.dto.TraceHostInfoDto;
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
public interface TraceHostInfoRepository extends CrudRepository<TraceHostInfo, String> {

    /**
     * 根据sql查询
     *
     * @return
     */
    @Query(value = "select new com.jthink.skyeye.data.jpa.dto.TraceHostInfoDto(a.mac, a.hostName, a.hostId, a .remark)"
            + " from TraceHostInfo a where a.mac=?1")
    public List<TraceHostInfoDto> findByMac(String mac);

    @Query(value = "select new com.jthink.skyeye.data.jpa.dto.TraceHostInfoDto(a.mac, a.hostName, a.hostId, a .remark)"
            + " from TraceHostInfo a where a.hostId=?1")
    public List<TraceHostInfoDto> findByHostId(Integer hostId);

}
