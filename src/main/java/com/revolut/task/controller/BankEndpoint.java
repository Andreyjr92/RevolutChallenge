package com.revolut.task.controller;

import com.revolut.task.connection_pool.TransactionalBlockingConnectionPool;
import com.revolut.task.dao.AccountDAO;
import com.revolut.task.dao.AllAccountsDAO;
import com.revolut.task.dto.AccountDTO;
import com.revolut.task.dto.TransactionDTO;
import com.revolut.task.exception.UserNotFoundException;
import com.revolut.task.logic.MoneyTransfer;
import com.revolut.task.model.Account;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

/**
 * <p>Endpoint to manage bank operations</p>
 */
@Path("bank")
public class BankEndpoint {

    private final TransactionalBlockingConnectionPool connectionPool = TransactionalBlockingConnectionPool.getInstance();

    @POST
    @Path("/transaction")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean transferMoney(TransactionDTO transactionDTO) {
        new MoneyTransfer(transactionDTO, connectionPool).transfer();
        return true;
    }

    @POST
    @Path("/deposit")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean deposit(AccountDTO accountDTO) {
        Optional<Account> accountOpt = new AccountDAO.Identified(accountDTO.getAccountId(), connectionPool).get();
        if (accountOpt.isPresent()) {
            new AccountDAO(accountOpt.get(), connectionPool).deposit(accountDTO.getAmount(), accountDTO.getCurrency());
        } else {
            new AccountDAO(new Account(accountDTO), connectionPool).submitNew();
        }
        return true;
    }

    @POST
    @Path("/withdraw")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean withdraw(AccountDTO accountDTO) {
        Optional<Account> accountOpt = new AccountDAO.Identified(accountDTO.getAccountId(), connectionPool).get();
        accountOpt.ifPresent(account -> new AccountDAO(account, connectionPool).withdraw(accountDTO.getAmount(), accountDTO.getCurrency()));
        return true;
    }

    @GET
    @Path("/account")
    @Produces(MediaType.APPLICATION_JSON)
    public Account account(@QueryParam("id") Long accountId) {
        Optional<Account> account = new AccountDAO.Identified(accountId, connectionPool).get();
        return account.orElseThrow(UserNotFoundException::new);
    }

    @GET
    @Path("/close")
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean closeAccount(@QueryParam("id") Long accountId) {
        return new AccountDAO.Identified(accountId, connectionPool).delete();
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> allAccouts() {
        Optional<List<Account>> accounts = new AllAccountsDAO(connectionPool).get();
        return accounts.orElse(null);
    }
}