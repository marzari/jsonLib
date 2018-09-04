package io.codelink.json.comparator;

import io.codelink.json.JElement;

import java.time.LocalDateTime;
import java.util.Comparator;

public class LocalDateTimeComparator implements Comparator<JElement>, ComparatorInterface {
	private String orderBy = "";
	private OrderType orderType;

	public LocalDateTimeComparator(String orderBy, OrderType orderType) {
		this.orderBy = orderBy;
		this.orderType = orderType;
	}

	@Override
	public int compare(JElement arg0, JElement arg1) {
		LocalDateTime first = arg0.asObject().dateTime(orderBy);
		LocalDateTime second = arg1.asObject().dateTime(orderBy);

		if (first != null && second != null) {
			return first.compareTo(second) * (orderType == OrderType.ASC ? 1 : -1);
		} else {
			return (orderType == OrderType.ASC ? 1 : -1);
		}
	}
}
