package br.usp.marketvc.beans;

import java.util.*;
import javax.xml.bind.annotation.*;
import javax.persistence.*;

@Entity
@Table(name="tinvestment")
public class Investment {
	public Investment() { }

	@Id
	@Column(name="iid", unique=true, nullable=false)
	@SequenceGenerator(name = "seq_iid", sequenceName = "seq_iid")
	@GeneratedValue(strategy=GenerationType.AUTO, generator="seq_iid")
	private Long iid;

	@Column(name="price")
    private Double price;

	@Column(name="amount")
    private Integer amount;

	@Column(name="selling")
    private Boolean selling;

	@ManyToOne
	@JoinColumn(name="email", nullable=true, updatable=false, insertable=false)
	private User owner;
	public User getOwner() { return this.owner; }
	public void setOwner(User owner) { this.owner = owner; }

	@ManyToOne
	@JoinColumn(name="symbol", nullable=true, updatable=false, insertable=false)
	private Stock stock;
	public Stock getStock() { return this.stock; }
	public void setStock(Stock stock) { this.stock = stock; }

	public Long getId() { return this.iid; }
    public Double getPrice() { return this.price; }
    public Integer getAmount() { return this.amount; }
    public Boolean getSelling() { return this.selling; }

	public void setPrice(Double price) { this.price = price; }
	public void setAmount(Integer amount) { this.amount = amount; }
	public void setSelling(Boolean selling) { this.selling = selling; }

}
