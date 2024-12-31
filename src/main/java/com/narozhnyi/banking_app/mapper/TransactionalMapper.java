package com.narozhnyi.banking_app.mapper;

import com.narozhnyi.banking_app.dto.transaction.PaymentDto;
import com.narozhnyi.banking_app.dto.transaction.TransactionalResponse;
import com.narozhnyi.banking_app.dto.transaction.TransferFundDto;
import com.narozhnyi.banking_app.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionalMapper {

  @Mapping(target = "sender.accountNumber", source = "senderAccountNumber")
  @Mapping(target = "receiver.accountNumber", source = "receiverAccountNumber")
  Transaction toTransaction(TransferFundDto transferFundDto);

  @Mapping(target = "receiver.accountNumber", source = "accountNumber")
  Transaction toAccountPaymentTransaction(PaymentDto depositFundDto);

  TransactionalResponse toReadDto(Transaction transaction);

  @Mapping(target = "accountNumber", source = "senderAccountNumber")
  @Mapping(target = "transactionType", constant = "TRANSFER")
  PaymentDto toDepositDto(TransferFundDto transferFundDto);

  @Mapping(target = "accountNumber", source = "receiverAccountNumber")
  @Mapping(target = "transactionType", constant = "TRANSFER")
  PaymentDto toWithdrawDto(TransferFundDto transferFundDto);

}
