package com.jthink.skyeye.data.jpa.repository;

import com.jthink.skyeye.data.jpa.domain.ServiceInfo;
import com.jthink.skyeye.data.jpa.pk.ServiceInfoPK;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2017-03-29 15:32:13
 */
public interface ServiceInfoRepository extends CrudRepository<ServiceInfo, ServiceInfoPK> {
    @Query(value = "select distinct s.serviceInfoPK.iface from ServiceInfo s")
    List<String> findAllIface();

    @Query(value = "select s.serviceInfoPK.method from ServiceInfo s where s.serviceInfoPK.iface=?")
    List<String> findMethodByIface(String iface);

}
