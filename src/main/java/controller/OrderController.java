package controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bean.OrderDetail;
import dao.OrderDAO;
import dto.OrderDTO;
import dto.OrderProductDTO;
import jsonobject.JSONOrderList;
import util.CommonUtil;

@RequestMapping(value = "order")

@CrossOrigin
@Transactional("tjtJTransactionManager")
@RestController
public class OrderController {

	private static final Logger logger = Logger.getLogger(OrderController.class);

	@Autowired
	OrderDAO orderDAO;

	@RequestMapping(value = "get_order", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONOrderList getOrder(@RequestParam(value = "user_id") Integer userId) {
		logger.info("getOrder() started with userId = " + userId);
		JSONOrderList jsonObject = new JSONOrderList();
		CopyOnWriteArrayList<OrderDTO> orderDTOLst = new CopyOnWriteArrayList<OrderDTO>();

		try {
			List<OrderDetail> orderList = orderDAO.getOrder(userId);
			CopyOnWriteArrayList<CopyOnWriteArrayList<OrderDetail>> tempList = new CopyOnWriteArrayList<CopyOnWriteArrayList<OrderDetail>>();
			if (!CommonUtil.isNullOrEmpty(orderList)) {
				CopyOnWriteArrayList<OrderDetail> loopList = new CopyOnWriteArrayList<OrderDetail>();
				int orderIdCheck = 0;
				if (orderList.size() == 1) {
					loopList.add(orderList.get(0));
					tempList.add(loopList);
				}else {
					for (int i = 0; i < orderList.size(); i++) {
						if (i == 0) {
							orderIdCheck = orderList.get(i).getOrderId();
						}
						if (orderIdCheck == orderList.get(i).getOrderId()) {
							loopList.add(orderList.get(i));
							if (i == orderList.size() - 1) {
								tempList.add(loopList);
							}
						} else {
							tempList.add(loopList);
							loopList = new CopyOnWriteArrayList<OrderDetail>();
							orderIdCheck = orderList.get(i).getOrderId();
							loopList.add(orderList.get(i));
							if (i == orderList.size() - 1) {
								tempList.add(loopList);
							}
						}
					}
				}
				

				for (CopyOnWriteArrayList<OrderDetail> listItem : tempList) {
					// Inner layer of array object
					OrderDTO orderDTO = new OrderDTO();
					CopyOnWriteArrayList<OrderProductDTO> orderProductDTOLst = new CopyOnWriteArrayList<OrderProductDTO>();
					for (OrderDetail orderItem : listItem) {
						orderDTO.setDate(new SimpleDateFormat("dd/MM/yyyy").format(orderItem.getCreatedDate()));
						orderDTO.setOrderId(orderItem.getOrderId());
						OrderProductDTO orderProductDTO = new OrderProductDTO();
						orderProductDTO.setProductId(orderItem.getProductId());
						orderProductDTO.setProductQuantity(orderItem.getProductQuantity());
						orderProductDTO.setProductPrice(new DecimalFormat("0.00").format(orderItem.getProductPrice()));
						orderProductDTO.setProductTitle(orderItem.getProductTitle());
						orderProductDTOLst.add(orderProductDTO);
					}
					orderDTO.setOrders(orderProductDTOLst);
					orderDTOLst.add(orderDTO);
				}

				jsonObject.setCode("S");
				jsonObject.setData(orderDTOLst);
				logger.info("getOrder() success with return value = " + jsonObject);
			} else {
				jsonObject.setCode("S");
				jsonObject.setDetail("No result is found.");
				jsonObject.setData(orderDTOLst);
				logger.error("getOrder() failed with error: no result is found.");
			}
		} catch (Exception e) {
			jsonObject.setCode("F");
			jsonObject.setDetail("Error occured: " + e.getMessage());
			logger.error("getOrder() failed with error: " + e.getMessage());
		}
		return jsonObject;
	}
}