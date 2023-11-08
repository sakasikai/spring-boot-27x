package org.springframework.boot.room.myBeans;

import org.springframework.boot.room.Helper.Describable;
import org.springframework.stereotype.Component;

/**
 * @title Zoo
 * @description TODO
 * @author maiqi
 * @create 2023/11/8 10:39
 */

@Component
public class Zoo implements Describable {

	String name;

	public void desc() {
		this.desc(Zoo.class.toString());
	}
}
