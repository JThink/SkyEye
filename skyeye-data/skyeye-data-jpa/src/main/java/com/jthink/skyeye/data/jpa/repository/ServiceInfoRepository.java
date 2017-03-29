package com.jthink.skyeye.data.jpa.repository;

import com.jthink.skyeye.data.jpa.domain.ServiceInfo;
import com.jthink.skyeye.data.jpa.pk.ServiceInfoPK;
import org.springframework.data.repository.CrudRepository;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2017-03-29 15:32:13
 */
public interface ServiceInfoRepository extends CrudRepository<ServiceInfo, ServiceInfoPK> {
}
