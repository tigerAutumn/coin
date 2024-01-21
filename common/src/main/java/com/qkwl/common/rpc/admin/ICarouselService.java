package com.qkwl.common.rpc.admin;

import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.carousel.ListCarouselReq;
import com.qkwl.common.dto.carousel.SaveCarouselReq;
import com.qkwl.common.dto.carousel.SystemCarousel;
import com.qkwl.common.dto.carousel.UpdateCarouselReq;

/**
 * 
 * @author huangjinfeng
 */
public interface ICarouselService {

	/**
	 * @param dto
	 * @return
	 */
	PageInfo<SystemCarousel> listSystemCarousel(ListCarouselReq dto);

	/**
	 * @param id
	 * @return
	 */
	SystemCarousel selectById(Integer id);

	/**
	 * @param req
	 */
	void saveCarousel(@Valid SaveCarouselReq req);

	/**
	 * @param req
	 */
	void updateCarousel(@Valid UpdateCarouselReq req);

	/**
	 * @param id
	 */
	void deleteCarousel(Integer id);



	
}
