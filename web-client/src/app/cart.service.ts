import { Injectable } from '@angular/core';
import { MessageService } from './message.service';
import { OrderService } from './order.service';
import { AngularFireAuth } from  "@angular/fire/auth";
import { Observable } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { LocalStorageService } from 'angular-2-local-storage';

@Injectable()
export class CartService {

  cart_products = [];
  order_creating_started = false;

  constructor(
    private message_service: MessageService,
    private order_service: OrderService,
    private afAuth: AngularFireAuth,
    private local_storage_service: LocalStorageService
  ) {
    if (this.local_storage_service.get('cart_products')) {
      this.cart_products = this.local_storage_service.get('cart_products');
    }
    
    this.order_service.get_stored_order().subscribe((is_order_exist) => {
      if (is_order_exist) {
        if (this.order_creating_started) {
          this.message_service.show_error('Не аозможно создать заказ, так как есть текущий!');
        }
      } else {
        if (this.order_creating_started) {
          this.create_order();
        }
      }
      this.order_creating_started = false;
    });
      
  }

  private comparing_addins(addins1, addins2) {
  	if (addins1.length !== addins2.length) {
  		return false;
  	}
  	if (JSON.stringify(addins1.map(addin => addin.id).sort()) === JSON.stringify(addins2.map(addin => addin.id).sort())) {
	    return true;
	}
  	return false;
  }

  public clear_cart() {
  	this.cart_products = [];

    this.local_storage_service.set('cart_products', this.cart_products);
  }

  public add_product(cart_product) {
  	let add_new = true;
  	this.cart_products.map(product => {
  		if (product.drink_id == cart_product.drink_id && product.size_id == cart_product.size_id && this.comparing_addins(cart_product.addins, product.addins)) { 
	    	product.qty += cart_product.qty;
	    	add_new = false;
	    }
  	});
  	if (add_new) {
  		this.cart_products.push(cart_product);
  	}
    this.local_storage_service.set('cart_products', this.cart_products);
    
    return true;
  }

  get_products() {
  	return this.cart_products;
  }

  get_products_qty() {
  	return this.cart_products.reduce(function(prev, cur) 
    {
        return prev + cur.qty;
    }, 0);
  }

  remove_product(index) {
  	if (index > -1) {
      this.cart_products.splice(index, 1);
      this.local_storage_service.set('cart_products', this.cart_products);

      return true;
	  }
    return false;
  }

  get_total_price() {
  	return this.cart_products.reduce(function(prev, cur) 
    {
        return prev + cur.qty * cur.price;
    }, 0);
  }

  create_order(): Observable<any> {
    return this.order_service.create_order(this.cart_products).pipe(
     tap((data: any) => {
        this.clear_cart();
      }));    
  }
}
