package com.jthink.skyeye.data.jpa.repository;

import com.jthink.skyeye.data.jpa.domain.NameInfo;
import com.jthink.skyeye.data.jpa.dto.NameInfoDto;
import com.jthink.skyeye.data.jpa.pk.NameInfoPK;
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
public interface NameInfoRepository extends CrudRepository<NameInfo, NameInfoPK> {

    @Query(value = "select new com.jthink.skyeye.data.jpa.dto.NameInfoDto(a.nameInfoPK.name, a.nameInfoPK.type, a.app)"
            + "from NameInfo a where a.nameInfoPK.type=?1")
    List<NameInfoDto> findBySql(String type);

    @Query(value = "select new com.jthink.skyeye.data.jpa.dto.NameInfoDto(a.nameInfoPK.name, a.nameInfoPK.type, a.app)"
            + "from NameInfo a where a.nameInfoPK.type=?1 and a.nameInfoPK.name=?2")
    List<NameInfoDto> findBySql(String type, String name);

    /**
     * 查询属于tid模板的所有name info
     * @param tid
     * @return
     */
    List<NameInfo> findByTid(Integer tid);
}
