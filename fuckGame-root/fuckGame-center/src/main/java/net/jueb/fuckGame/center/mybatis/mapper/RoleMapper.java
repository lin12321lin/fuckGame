package net.jueb.fuckGame.center.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import net.jueb.fuckGame.center.base.JsonConfigTypeHandler;
import net.jueb.fuckGame.core.common.dto.Role;

public interface RoleMapper {

	public final static String TABLE_NAME="t_role";
	
	public final String JsonConfigTypeHandler="net.xzmj.center.base.JsonConfigTypeHandler";
	
	@Select("select max(id) from "+TABLE_NAME+"")
	public int getMaxRoleId();
	
	@Options(useGeneratedKeys=true,keyProperty="id")
    @Insert("insert into "+TABLE_NAME+"("
    		+ "id,uid,name,money,sex,phone,faceIcon,config,"
    		+ "status,createTime,lastLogin"
    		+ ") "
    		+ "values("
    		+ "#{id},#{uid},#{name},#{money},#{sex},#{phone},#{faceIcon},#{config,typeHandler="+JsonConfigTypeHandler+"},"
    		+ "#{status},#{createTime},#{lastLogin}"
    		+ ")")  
    public int insert(Role role);  
      
    @Update("update "+TABLE_NAME+" set uid=#{uid},name = #{name},money = #{money},sex = #{sex},"
    		+ " phone = #{phone},faceIcon=#{faceIcon},config=#{config,typeHandler="+JsonConfigTypeHandler+"},"
    		+ " status = #{status},createTime = #{createTime},lastLogin = #{lastLogin}"
    		+ " where id = #{id}")  
    public int update(Role role);
    
    @Results(value = {
			  @Result(property = "config", column = "config",typeHandler=JsonConfigTypeHandler.class),
			})
    @Select("select * from "+TABLE_NAME+" where id = #{id}")
    public Role findRoleById(long id); 
    
    @Results(value = {
			  @Result(property = "config", column = "config",typeHandler=JsonConfigTypeHandler.class),
			})
    @Select("select * from "+TABLE_NAME+" where uid = #{uid}")
    public Role findRoleByUid(String uid); 
    
    @Results(value = {
			  @Result(property = "config", column = "config",typeHandler=JsonConfigTypeHandler.class),
			})
    @Select("select * from "+TABLE_NAME+" where name = #{name}")  
    public List<Role> findRoleByName(String name); 
    
    @Delete("delete from "+TABLE_NAME+" where id = #{id}")  
    public int deleteById(long id); 
    
    @Results(value = {
			  @Result(property = "config", column = "config",typeHandler=JsonConfigTypeHandler.class),
			})
    @Select("select * from "+TABLE_NAME+"")  
    public List<Role> findAll(); 
}
