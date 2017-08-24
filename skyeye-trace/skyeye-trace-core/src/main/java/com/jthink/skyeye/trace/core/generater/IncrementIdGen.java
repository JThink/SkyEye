package com.jthink.skyeye.trace.core.generater;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.trace.core.dto.RegisterDto;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 自增长ID生成器，用来给app和host的服务进行编号（利用zk节点的版本号每写一次就自增的机制来实现）
 * @date 2017-03-24 11:25:31
 */
public class IncrementIdGen implements IdGen {

    // 为某台机器上的某个项目分配的serviceId（注意区分Span中的serviceId）
    private static String serviceId = null;

    // register info
    private RegisterDto registerDto;

    /**
     * 利用zookeeper
     * @return
     */
    @Override
    public String nextId() {
        String app = this.registerDto.getApp();
        String host = this.registerDto.getHost();
        ZkClient zkClient = this.registerDto.getZkClient();
        String path = Constants.ZK_REGISTRY_ID_ROOT_PATH + Constants.SLASH + app + Constants.SLASH + host;
        if (zkClient.exists(path)) {
            // 如果已经有该节点，表示已经为当前的host上部署的该app分配的编号（应对某个服务重启之后编号不变的问题），直接获取该id，而无需生成
            return zkClient.readData(Constants.ZK_REGISTRY_ID_ROOT_PATH + Constants.SLASH + app + Constants.SLASH + host);
        } else {
            // 节点不存在，那么需要生成id，利用zk节点的版本号每写一次就自增的机制来实现
            Stat stat = zkClient.writeDataReturnStat(Constants.ZK_REGISTRY_SEQ, new byte[0], -1);
            // 生成id
            String id = String.valueOf(stat.getVersion());
            // 将数据写入节点
            zkClient.createPersistent(path, true);
            zkClient.writeData(path, id);
            return id;
        }
    }

    /**
     * 获取ID
     * @return
     */
    public static String getId() {
        return serviceId;
    }

    /**
     * 对ID赋值
     * @param id
     * @return
     */
    public static void setId(String id) {
        serviceId = id;
    }

    public IncrementIdGen() {

    }

    public IncrementIdGen(RegisterDto registerDto) {
        this.registerDto = registerDto;
    }

    public RegisterDto getRegisterDto() {
        return registerDto;
    }

    public IncrementIdGen setRegisterDto(RegisterDto registerDto) {
        this.registerDto = registerDto;
        return this;
    }
}
