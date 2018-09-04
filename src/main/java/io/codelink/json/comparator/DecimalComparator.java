package io.codelink.json.comparator;

import io.codelink.json.JElement;

import java.util.Comparator;

public class DecimalComparator implements Comparator<JElement>, ComparatorInterface {
	private String orderBy = "";
	private OrderType orderType;

	public DecimalComparator(String orderBy, OrderType orderType) {
		this.orderBy = orderBy;
		this.orderType = orderType;
	}

	@Override
	public int compare(JElement arg0, JElement arg1) {
		return arg0.asObject().decimal(orderBy).compareTo(arg1.asObject().decimal(orderBy)) * (orderType == OrderType.ASC ? 1 : -1);
	}
}
