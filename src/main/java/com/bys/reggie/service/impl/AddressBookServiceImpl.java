package com.bys.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bys.reggie.entity.AddressBook;
import com.bys.reggie.service.AddressBookService;
import com.bys.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-09-16 08:33:33
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




