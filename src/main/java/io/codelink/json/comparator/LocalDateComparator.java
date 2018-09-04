package io.codelink.json.comparator;

import io.codelink.json.JElement;

import java.time.LocalDate;
import java.util.Comparator;

public class LocalDateComparator implements Comparator<JElement>, ComparatorInterface {
	private String orderBy = "";
	private OrderType orderType;

	public LocalDateComparator(String orderBy, OrderType orderType) {
		this.orderBy = orderBy;
		this.orderType = orderType;
	}

	@Override
	public int compare(JElement arg0, JElement arg1) {
		LocalDate first = arg0.asObject().date(orderBy);
		LocalDate second = arg1.asObject().date(orderBy);

		if (first != null && second != null) {
			return first.compareTo(second) * (orderType == OrderType.ASC ? 1 : -1);
		} else {
			return (orderType == OrderType.ASC ? 1 : -1);
		}
	}
}
