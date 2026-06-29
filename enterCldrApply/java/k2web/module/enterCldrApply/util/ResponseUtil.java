package k2web.module.enterCldrApply.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * HttpServletResponse로 응답을 보낼 때 사용하는 유틸 클래스
 * <pre>
 * << 개정이력(Modification Information) >>
 *    2023.04.12 : JSH : 최초 생성
 *    2024.03.01 : JG : 위자드 고도화
 * </pre>
 * @since 2023.04.12 
 * @author JSH
 * @version 2.0
 */
public class ResponseUtil {
	
	/** 로그 */
	private static final Logger LOG = LoggerFactory.getLogger(ResponseUtil.class);
	
	/** 날짜 형식 */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * HttpServletResponse를 통해 데이터를 JSON 형식으로 응답한다
	 * @param response javax.servlet.http.HttpServletResponse
	 * @param valueObj JSON 형식으로 응답할 Value 객체
	 * @return true : JSON 응답 성공, false : JSON 응답 실패
	 * @see #makeJsonObject(Object)
	 * @see com.google.gson.JsonElement
	 * @see com.google.gson.JsonObject
	 * @see java.io.PrintWriter
	 * @exception java.io.IOException
	 */
	public static boolean setJsonDataToResponse(HttpServletResponse response, Object valueObj) {
		JsonElement result = valueObj != null? makeJsonObject(valueObj) : new JsonObject();
		response.setContentType( "application/json" );
		response.setHeader( "Content-Type","text/json" );
		response.setCharacterEncoding( "UTF-8" );
		PrintWriter writer = null;
		boolean isSuccess = true;
		try {
			writer = response.getWriter();
			writer.write( result.toString() );
		} catch (IOException e) {
			isSuccess = false;
		} 
		return isSuccess;
	}
	
	/**
	 * 데이터가 존재하는 Value 객체를 JSON 객체로 변환한다.
	 * @param valueObj 데이터가 존재하는 Value 객체 
	 * @return 변환된 JsonElement
	 * @see #checkAvailJsonType(Object)
	 * @see #addPropertyToJsonObject(JsonObject, String, Object)
	 * @see #addToJsonArray(JsonArray, Object)
	 * @see com.google.gson.JsonElement
	 * @see com.google.gson.JsonObject
	 * @see java.util.Collection
	 * @see java.util.Map
	 * @see java.lang.reflect.Field
	 * @exception java.lang.SecurityException
	 * @exception java.lang.IllegalArgumentException
	 * @exception java.lang.IllegalAccessException
	 */
	public static JsonElement makeJsonObject(Object valueObj) {
		if(valueObj == null)	return null;
		JsonElement jsonEle = new JsonObject();
		if(checkAvailJsonType(valueObj)) {
			if(valueObj instanceof JsonElement) {
				jsonEle = (JsonElement) valueObj;
			} else {
				addPropertyToJsonObject((JsonObject)jsonEle, "value", valueObj);
			}
		} else if(valueObj instanceof Collection<?>) { //Collection형
			JsonArray jsonArr = new JsonArray();
			Collection<?> tmpValueObj = (Collection<?>) valueObj;
			if(!tmpValueObj.isEmpty()) {
				for(Object eleObj : tmpValueObj) {
					if(checkAvailJsonType(eleObj))	addToJsonArray(jsonArr, eleObj);
					else							jsonArr.add(makeJsonObject(eleObj));
				}
				jsonEle = jsonArr;
			}
		} else if(valueObj instanceof Map<?,?>) { //Map형
			JsonObject jsonObj = new JsonObject();
			Map<?,?> tmpValueObj = (Map<?,?>) valueObj;
			if(!tmpValueObj.isEmpty()) {
				Set<?> keySet = tmpValueObj.keySet();
				String key = null;
				Object eleObj = null;
				for(Object k : keySet) {
					key = String.valueOf(k);
					eleObj = tmpValueObj.get(k);
					if(checkAvailJsonType(eleObj))	addPropertyToJsonObject(jsonObj, key, eleObj);
					else							jsonObj.add(key, makeJsonObject(eleObj));							
				}
				jsonEle = jsonObj;
			}
		} else if(valueObj.getClass().isArray()) { //배열형
			JsonArray jsonArr = new JsonArray();
			Object[] tmpValueObj = (Object[]) valueObj;
			if(tmpValueObj.length > 0) {
				for(Object eleObj : tmpValueObj) {
					if(checkAvailJsonType(eleObj))	addToJsonArray(jsonArr, eleObj);
					else							jsonArr.add(makeJsonObject(eleObj));
				}
				jsonEle = jsonArr;
			}
		} else { //기타객체(모델 포함)
			Class<?> clazz = valueObj.getClass();
			Field[] fields = clazz.getDeclaredFields();
			if(fields!=null && fields.length>0) {
				Map<String, Object> fieldMap = new HashMap<String, Object>();
				String fieldNm = null;
				Object fieldValue = null;
				String packageNm = null;
				for(Field field : fields) {
					try {
						if(Modifier.isStatic(field.getModifiers()))		continue;
						
						field.setAccessible(true);
						fieldNm = field.getName();
						fieldValue = field.get(valueObj);
						if(fieldValue == null)							continue;
						
						if(fieldValue.getClass()==null) 				continue;
						if(fieldValue.getClass().getPackage()==null) 	continue;
						
						packageNm = fieldValue.getClass().getPackage().getName();
						if(!(fieldValue instanceof Collection<?>)
							&& !(fieldValue instanceof Map<?,?>)
							&& !(fieldValue.getClass().isArray())
							&& !packageNm.startsWith("k2web")) {
							if(fieldValue instanceof Date) {
								fieldValue = DATE_FORMAT.format((Date) fieldValue);
							} else {
								fieldValue = fieldValue.toString();
							}
						}
						fieldMap.put(fieldNm, fieldValue);
					} catch (SecurityException e) {
						LOG.error("ERROR:",e);
						continue;
					} catch (IllegalArgumentException e) {
						LOG.error("ERROR:",e);
						continue;
					} catch (IllegalAccessException e) {
						LOG.error("ERROR:",e);
						continue;
					}
				}
				jsonEle = makeJsonObject(fieldMap);
			}
		}
		return jsonEle;
	}
	
	/**
	 * 해당 변수가 JSON 형식의 타입인지 확인한다. 
	 * @param obj 값이 존재하는 객체
	 * @return true : JSON 형식 타입(숫자/문자/Json요소/참거짓)의 변수, false : 그 밖의 변수
	 * @see com.google.gson.JsonElement
	 */
	private static boolean checkAvailJsonType(Object obj) {
		return obj == null? false
							: (
								obj instanceof Number
								|| obj instanceof Boolean
								|| obj instanceof String
								|| obj instanceof Character
								|| obj instanceof JsonElement
							);
	}
	
	/**
	 * JsonObject에 값을 추가한다.
	 * @param jsonObj com.google.gson.JsonObject
	 * @param key JsonObject에 추가할 키
	 * @param value JsonObject에 추가할 값
	 * @see #checkAvailJsonType(Object)
	 * @see com.google.gson.JsonObject
	 * @see com.google.gson.JsonElement
	 */
	private static void addPropertyToJsonObject(JsonObject jsonObj, String key, Object value) {
		if(jsonObj == null || value == null || !checkAvailJsonType(value))	return;
		
		if(value instanceof Number)				jsonObj.addProperty(key, (Number)value);
		else if(value instanceof Boolean)		jsonObj.addProperty(key, (Boolean)value);
		else if(value instanceof String)		jsonObj.addProperty(key, (String)value);
		else if(value instanceof Character)		jsonObj.addProperty(key, (Character)value);
		else									jsonObj.add(key, (JsonElement)value);
	}
	
	/**
	 * JsonArray에 값을 추가한다.
	 * @param jsonArr com.google.gson.JsonArray
	 * @param value JsonArray에 추가할 값
	 * @see #checkAvailJsonType(Object)
	 * @see com.google.gson.JsonElement
	 */
	private static void addToJsonArray(JsonArray jsonArr, Object value) {
		if(jsonArr == null || value == null || !checkAvailJsonType(value))	return;
		if(value instanceof Number)				jsonArr.add((Number)value);
		else if(value instanceof Boolean)		jsonArr.add((Boolean)value);
		else if(value instanceof String)		jsonArr.add((String)value);
		else if(value instanceof Character)		jsonArr.add((Character)value);
		else									jsonArr.add((JsonElement)value);
	}
	
}
