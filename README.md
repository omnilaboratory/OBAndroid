# OBAndroid


OBAndroid is a self-custodial [OmniBOLT](https://github.com/omnilaboratory/obd) Lightning wallet for android devices. It provides a platform for simple, instantaneous Bitcoin/Omnilayer asset payments. OBAndroid is now in developer beta, and will be in public beta in Nov/Dec 2022, available on Android only.  

<p align="center">
  <img width="500" alt="obwallet screenshots" src="https://github.com/omnilaboratory/obd/blob/master/docs/prototype/obwalletscreenshots.png">
</p>

## Features

- [x] Self-custodial
- [x] obd-lnd on Android
- [x] Mainnet, testnet, regtest
- [x] Bitcoin/Omnilayer wallet(layer 1): store, pay, receive, backup, restore
- [x] Bitcoin lightning payment(layer 2)  
- [x] Omnilayer assets lightning payment(layer 2), e.g. OMNI, USDT, etc  
- [x] Manage local and remote nodes
- [x] Create and fund channels by any Omnilayer assets
- [x] Management liquidity   
- [x] Create, manage invoices( [OmniBOLT 7](https://github.com/omnilaboratory/OmniBOLT-spec/blob/master/OmniBOLT-07-Hierarchical-Deterministic-(HD)-wallet.md#invoice-encoding))  
- [x] Pay invoices ( [OmniBOLT 7](https://github.com/omnilaboratory/OmniBOLT-spec/blob/master/OmniBOLT-07-Hierarchical-Deterministic-(HD)-wallet.md#invoice-encoding)) 
- [x] Cloud and local backup for channels  

- [ ] DEX in wallet
- [ ] Marketplace
- [ ] Trading histroy 

## System Requirements

* Android 8+ 64bits

## Compatibility

Full OBAndroid functionality depends on running a certain version of obd on mobile devices. View the table below to ensure that you run the correct version of obd with the relevant OBAndroid release. The bundled version will always come with the correct, compatible versioning. To connect to remote obd nodes, you should check the compatibility dependency below. 

The currently supported invoice format is [OmniBOLT 7](https://github.com/omnilaboratory/OmniBOLT-spec/blob/master/OmniBOLT-07-Hierarchical-Deterministic-(HD)-wallet.md#invoice-encoding). Paying btc/satoshi to a BOLT 11 invoice will cause the payment to fail.  


| OBWallet		|	obd				  |	 
| -------- 	  |	----------- |	 
| v0.1-alpha	|	to be added	|	 
 
## Get Help
If you encounter any issues please report the issue in this [repo](https://github.com/omnilaboratory/OBAndroid/issues) or on [discord](http://discord.gg/2QYqzSMZuy) with screenshots and how to reproduce the bug/error.

## License
This project is open source under the MIT license, which means you have full access to the source code and can modify it to fit your own needs. See LICENSE for more information.
