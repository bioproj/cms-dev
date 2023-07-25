package com.wangyang.service;

import com.wangyang.pojo.entity.Task;
import com.wangyang.pojo.enums.TaskType;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

public interface ITaskService extends ICrudService<Task,Task, BaseVo,Integer> {
    Task findByENName(TaskType taskType, String enName);
}
