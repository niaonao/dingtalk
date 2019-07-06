package pers.niaonao.dingtalkrobot.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @className: DataValidUtil
 * @description: 数据基础校验工具
 * @author: niaonao
 * @date: 2019/7/6
 **/
@Slf4j
public class DataValidUtil {
    /**
     * @description: 判空方法
     * @param obj
     * @return: boolean
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static boolean checkNotEmpty(Object... obj) {
        for (Object object : obj) {
            if (null == object) {
                return false;
            }
        }
        return true;
    }

    /**
     * @description: 校验接口入参对象方法
     *      校验object 对象是否全部属性都不为null
     * @param object
     * @return: boolean
     * @author: niaonao
     * @date: 2019/7/6
     */
    public static boolean checkExistEmpty(Object object) {
        if (null == object) {
            return false;
        }
        boolean flag = false;
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            //设置权限, 获取private的属性
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(object);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("对象属性值为空校验, 异常捕获{}", e.getMessage());
            }
            //只要有一个属性值不为null 就返回false 表示对象不为null
            if (null == fieldValue) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
