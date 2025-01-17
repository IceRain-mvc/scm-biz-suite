
package com.doublechaintech.retailscm.warehouse;















import com.doublechaintech.retailscm.*;import com.doublechaintech.retailscm.BaseViewPage;import com.doublechaintech.retailscm.RetailscmUserContextImpl;import com.doublechaintech.retailscm.damagespace.DamageSpace;import com.doublechaintech.retailscm.iamservice.*;import com.doublechaintech.retailscm.receivingspace.ReceivingSpace;import com.doublechaintech.retailscm.retailstorecountrycenter.CandidateRetailStoreCountryCenter;import com.doublechaintech.retailscm.retailstorecountrycenter.RetailStoreCountryCenter;import com.doublechaintech.retailscm.secuser.SecUser;import com.doublechaintech.retailscm.services.IamService;import com.doublechaintech.retailscm.shippingspace.ShippingSpace;import com.doublechaintech.retailscm.smartpallet.SmartPallet;import com.doublechaintech.retailscm.storagespace.StorageSpace;import com.doublechaintech.retailscm.supplierspace.SupplierSpace;import com.doublechaintech.retailscm.tree.*;import com.doublechaintech.retailscm.treenode.*;import com.doublechaintech.retailscm.userapp.UserApp;import com.doublechaintech.retailscm.utils.ModelAssurance;import com.doublechaintech.retailscm.warehouse.Warehouse;import com.doublechaintech.retailscm.warehouseasset.WarehouseAsset;
import com.terapico.caf.BlobObject;import com.terapico.caf.DateTime;import com.terapico.caf.Images;import com.terapico.caf.Password;import com.terapico.caf.baseelement.PlainText;import com.terapico.caf.viewpage.SerializeScope;
import com.terapico.uccaf.BaseUserContext;
import com.terapico.utils.*;
import java.math.BigDecimal;
import java.util.*;
import com.doublechaintech.retailscm.search.Searcher;


public class WarehouseManagerImpl extends CustomRetailscmCheckerManager implements WarehouseManager, BusinessHandler{

	// Only some of ods have such function
	
	// To test
	public BlobObject exportExcelFromList(RetailscmUserContext userContext, String id, String listName) throws Exception {

		Map<String,Object> tokens = WarehouseTokens.start().withTokenFromListName(listName).done();
		Warehouse  warehouse = (Warehouse) this.loadWarehouse(userContext, id, tokens);
		//to enrich reference object to let it show display name
		List<BaseEntity> entityListToNaming = warehouse.collectRefercencesFromLists();
		warehouseDaoOf(userContext).alias(entityListToNaming);

		return exportListToExcel(userContext, warehouse, listName);

	}
	@Override
	public BaseGridViewGenerator gridViewGenerator() {
		return new WarehouseGridViewGenerator();
	}
	




  


	private static final String SERVICE_TYPE = "Warehouse";
	@Override
	public WarehouseDAO daoOf(RetailscmUserContext userContext) {
		return warehouseDaoOf(userContext);
	}

	@Override
	public String serviceFor(){
		return SERVICE_TYPE;
	}



	protected void throwExceptionWithMessage(String value) throws WarehouseManagerException{

		Message message = new Message();
		message.setBody(value);
		throw new WarehouseManagerException(message);

	}



 	protected Warehouse saveWarehouse(RetailscmUserContext userContext, Warehouse warehouse, String [] tokensExpr) throws Exception{
 		//return getWarehouseDAO().save(warehouse, tokens);

 		Map<String,Object>tokens = parseTokens(tokensExpr);

 		return saveWarehouse(userContext, warehouse, tokens);
 	}

 	protected Warehouse saveWarehouseDetail(RetailscmUserContext userContext, Warehouse warehouse) throws Exception{


 		return saveWarehouse(userContext, warehouse, allTokens());
 	}

 	public Warehouse loadWarehouse(RetailscmUserContext userContext, String warehouseId, String [] tokensExpr) throws Exception{

 		checkerOf(userContext).checkIdOfWarehouse(warehouseId);

		checkerOf(userContext).throwExceptionIfHasErrors( WarehouseManagerException.class);



 		Map<String,Object>tokens = parseTokens(tokensExpr);

 		Warehouse warehouse = loadWarehouse( userContext, warehouseId, tokens);
 		//do some calc before sent to customer?
 		return present(userContext,warehouse, tokens);
 	}


 	 public Warehouse searchWarehouse(RetailscmUserContext userContext, String warehouseId, String textToSearch,String [] tokensExpr) throws Exception{

 		checkerOf(userContext).checkIdOfWarehouse(warehouseId);

		checkerOf(userContext).throwExceptionIfHasErrors( WarehouseManagerException.class);



 		Map<String,Object>tokens = tokens().allTokens().searchEntireObjectText(tokens().startsWith(), textToSearch).initWithArray(tokensExpr);

 		Warehouse warehouse = loadWarehouse( userContext, warehouseId, tokens);
 		//do some calc before sent to customer?
 		return present(userContext,warehouse, tokens);
 	}



 	protected Warehouse present(RetailscmUserContext userContext, Warehouse warehouse, Map<String, Object> tokens) throws Exception {


		addActions(userContext,warehouse,tokens);
    

		Warehouse  warehouseToPresent = warehouseDaoOf(userContext).present(warehouse, tokens);

		List<BaseEntity> entityListToNaming = warehouseToPresent.collectRefercencesFromLists();
		warehouseDaoOf(userContext).alias(entityListToNaming);


		renderActionForList(userContext,warehouse,tokens);

		return  warehouseToPresent;


	}



 	public Warehouse loadWarehouseDetail(RetailscmUserContext userContext, String warehouseId) throws Exception{
 		Warehouse warehouse = loadWarehouse( userContext, warehouseId, allTokens());
 		return present(userContext,warehouse, allTokens());

 	}

	public Object prepareContextForUserApp(BaseUserContext userContext,Object targetUserApp) throws Exception{
		
        UserApp userApp=(UserApp) targetUserApp;
        return this.view ((RetailscmUserContext)userContext,userApp.getAppId());
        
    }

	


 	public Object view(RetailscmUserContext userContext, String warehouseId) throws Exception{
 		Warehouse warehouse = loadWarehouse( userContext, warehouseId, viewTokens());
 		markVisited(userContext, warehouse);
 		return present(userContext,warehouse, viewTokens());

	 }
	 public Object summaryView(RetailscmUserContext userContext, String warehouseId) throws Exception{
		Warehouse warehouse = loadWarehouse( userContext, warehouseId, viewTokens());
		warehouse.summarySuffix();
		markVisited(userContext, warehouse);
 		return present(userContext,warehouse, summaryTokens());

	}
	 public Object analyze(RetailscmUserContext userContext, String warehouseId) throws Exception{
		Warehouse warehouse = loadWarehouse( userContext, warehouseId, analyzeTokens());
		markVisited(userContext, warehouse);
		return present(userContext,warehouse, analyzeTokens());

	}
 	protected Warehouse saveWarehouse(RetailscmUserContext userContext, Warehouse warehouse, Map<String,Object>tokens) throws Exception{
 	
 		return warehouseDaoOf(userContext).save(warehouse, tokens);
 	}
 	protected Warehouse loadWarehouse(RetailscmUserContext userContext, String warehouseId, Map<String,Object>tokens) throws Exception{
		checkerOf(userContext).checkIdOfWarehouse(warehouseId);

		checkerOf(userContext).throwExceptionIfHasErrors( WarehouseManagerException.class);



 		return warehouseDaoOf(userContext).load(warehouseId, tokens);
 	}

	







 	protected<T extends BaseEntity> void addActions(RetailscmUserContext userContext, Warehouse warehouse, Map<String, Object> tokens){
		super.addActions(userContext, warehouse, tokens);

		addAction(userContext, warehouse, tokens,"@create","createWarehouse","createWarehouse/","main","primary");
		addAction(userContext, warehouse, tokens,"@update","updateWarehouse","updateWarehouse/"+warehouse.getId()+"/","main","primary");
		addAction(userContext, warehouse, tokens,"@copy","cloneWarehouse","cloneWarehouse/"+warehouse.getId()+"/","main","primary");

		addAction(userContext, warehouse, tokens,"warehouse.transfer_to_owner","transferToAnotherOwner","transferToAnotherOwner/"+warehouse.getId()+"/","main","primary");
		addAction(userContext, warehouse, tokens,"warehouse.addStorageSpace","addStorageSpace","addStorageSpace/"+warehouse.getId()+"/","storageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.removeStorageSpace","removeStorageSpace","removeStorageSpace/"+warehouse.getId()+"/","storageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.updateStorageSpace","updateStorageSpace","updateStorageSpace/"+warehouse.getId()+"/","storageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.copyStorageSpaceFrom","copyStorageSpaceFrom","copyStorageSpaceFrom/"+warehouse.getId()+"/","storageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.addSmartPallet","addSmartPallet","addSmartPallet/"+warehouse.getId()+"/","smartPalletList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.removeSmartPallet","removeSmartPallet","removeSmartPallet/"+warehouse.getId()+"/","smartPalletList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.updateSmartPallet","updateSmartPallet","updateSmartPallet/"+warehouse.getId()+"/","smartPalletList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.copySmartPalletFrom","copySmartPalletFrom","copySmartPalletFrom/"+warehouse.getId()+"/","smartPalletList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.addSupplierSpace","addSupplierSpace","addSupplierSpace/"+warehouse.getId()+"/","supplierSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.removeSupplierSpace","removeSupplierSpace","removeSupplierSpace/"+warehouse.getId()+"/","supplierSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.updateSupplierSpace","updateSupplierSpace","updateSupplierSpace/"+warehouse.getId()+"/","supplierSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.copySupplierSpaceFrom","copySupplierSpaceFrom","copySupplierSpaceFrom/"+warehouse.getId()+"/","supplierSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.addReceivingSpace","addReceivingSpace","addReceivingSpace/"+warehouse.getId()+"/","receivingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.removeReceivingSpace","removeReceivingSpace","removeReceivingSpace/"+warehouse.getId()+"/","receivingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.updateReceivingSpace","updateReceivingSpace","updateReceivingSpace/"+warehouse.getId()+"/","receivingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.copyReceivingSpaceFrom","copyReceivingSpaceFrom","copyReceivingSpaceFrom/"+warehouse.getId()+"/","receivingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.addShippingSpace","addShippingSpace","addShippingSpace/"+warehouse.getId()+"/","shippingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.removeShippingSpace","removeShippingSpace","removeShippingSpace/"+warehouse.getId()+"/","shippingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.updateShippingSpace","updateShippingSpace","updateShippingSpace/"+warehouse.getId()+"/","shippingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.copyShippingSpaceFrom","copyShippingSpaceFrom","copyShippingSpaceFrom/"+warehouse.getId()+"/","shippingSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.addDamageSpace","addDamageSpace","addDamageSpace/"+warehouse.getId()+"/","damageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.removeDamageSpace","removeDamageSpace","removeDamageSpace/"+warehouse.getId()+"/","damageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.updateDamageSpace","updateDamageSpace","updateDamageSpace/"+warehouse.getId()+"/","damageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.copyDamageSpaceFrom","copyDamageSpaceFrom","copyDamageSpaceFrom/"+warehouse.getId()+"/","damageSpaceList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.addWarehouseAsset","addWarehouseAsset","addWarehouseAsset/"+warehouse.getId()+"/","warehouseAssetList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.removeWarehouseAsset","removeWarehouseAsset","removeWarehouseAsset/"+warehouse.getId()+"/","warehouseAssetList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.updateWarehouseAsset","updateWarehouseAsset","updateWarehouseAsset/"+warehouse.getId()+"/","warehouseAssetList","primary");
		addAction(userContext, warehouse, tokens,"warehouse.copyWarehouseAssetFrom","copyWarehouseAssetFrom","copyWarehouseAssetFrom/"+warehouse.getId()+"/","warehouseAssetList","primary");






	}// end method of protected<T extends BaseEntity> void addActions(RetailscmUserContext userContext, Warehouse warehouse, Map<String, Object> tokens){








  @Override
  public List<Warehouse> searchWarehouseList(RetailscmUserContext ctx, WarehouseRequest pRequest){
      pRequest.setUserContext(ctx);
      List<Warehouse> list = daoOf(ctx).search(pRequest);
      Searcher.enhance(list, pRequest);
      return list;
  }

  @Override
  public Warehouse searchWarehouse(RetailscmUserContext ctx, WarehouseRequest pRequest){
    pRequest.limit(0, 1);
    List<Warehouse> list = searchWarehouseList(ctx, pRequest);
    if (list == null || list.isEmpty()){
      return null;
    }
    return list.get(0);
  }

	public Warehouse createWarehouse(RetailscmUserContext userContext, String location,String contactNumber,String totalArea,String ownerId,BigDecimal latitude,BigDecimal longitude,String contract) throws Exception
	{





		checkerOf(userContext).checkLocationOfWarehouse(location);
		checkerOf(userContext).checkContactNumberOfWarehouse(contactNumber);
		checkerOf(userContext).checkTotalAreaOfWarehouse(totalArea);
		checkerOf(userContext).checkLatitudeOfWarehouse(latitude);
		checkerOf(userContext).checkLongitudeOfWarehouse(longitude);
		checkerOf(userContext).checkContractOfWarehouse(contract);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



		Warehouse warehouse=createNewWarehouse();	

		warehouse.setLocation(location);
		warehouse.setContactNumber(contactNumber);
		warehouse.setTotalArea(totalArea);
			
		RetailStoreCountryCenter owner = loadRetailStoreCountryCenter(userContext, ownerId,emptyOptions());
		warehouse.setOwner(owner);
		
		
		warehouse.setLatitude(latitude);
		warehouse.setLongitude(longitude);
		warehouse.setContract(contract);
		warehouse.setLastUpdateTime(userContext.now());

		warehouse = saveWarehouse(userContext, warehouse, emptyOptions());
		
		onNewInstanceCreated(userContext, warehouse);
		return warehouse;


	}
	protected Warehouse createNewWarehouse()
	{

		return new Warehouse();
	}

	protected void checkParamsForUpdatingWarehouse(RetailscmUserContext userContext,String warehouseId, int warehouseVersion, String property, String newValueExpr,String [] tokensExpr)throws Exception
	{
		



		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkVersionOfWarehouse( warehouseVersion);


		if(Warehouse.LOCATION_PROPERTY.equals(property)){
		
			checkerOf(userContext).checkLocationOfWarehouse(parseString(newValueExpr));
		

		}
		if(Warehouse.CONTACT_NUMBER_PROPERTY.equals(property)){
		
			checkerOf(userContext).checkContactNumberOfWarehouse(parseString(newValueExpr));
		

		}
		if(Warehouse.TOTAL_AREA_PROPERTY.equals(property)){
		
			checkerOf(userContext).checkTotalAreaOfWarehouse(parseString(newValueExpr));
		

		}

		
		if(Warehouse.LATITUDE_PROPERTY.equals(property)){
		
			checkerOf(userContext).checkLatitudeOfWarehouse(parseBigDecimal(newValueExpr));
		

		}
		if(Warehouse.LONGITUDE_PROPERTY.equals(property)){
		
			checkerOf(userContext).checkLongitudeOfWarehouse(parseBigDecimal(newValueExpr));
		

		}
		if(Warehouse.CONTRACT_PROPERTY.equals(property)){
		
			checkerOf(userContext).checkContractOfWarehouse(parseString(newValueExpr));
		

		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}



	public Warehouse clone(RetailscmUserContext userContext, String fromWarehouseId) throws Exception{

		return warehouseDaoOf(userContext).clone(fromWarehouseId, this.allTokens());
	}

	public Warehouse internalSaveWarehouse(RetailscmUserContext userContext, Warehouse warehouse) throws Exception
	{
		return internalSaveWarehouse(userContext, warehouse, allTokens());

	}
	public Warehouse internalSaveWarehouse(RetailscmUserContext userContext, Warehouse warehouse, Map<String,Object> options) throws Exception
	{
		//checkParamsForUpdatingWarehouse(userContext, warehouseId, warehouseVersion, property, newValueExpr, tokensExpr);


		synchronized(warehouse){
			//will be good when the warehouse loaded from this JVM process cache.
			//also good when there is a ram based DAO implementation
			//make changes to Warehouse.
			if (warehouse.isChanged()){
			warehouse.updateLastUpdateTime(userContext.now());
			}

      //checkerOf(userContext).checkAndFixWarehouse(warehouse);
			warehouse = saveWarehouse(userContext, warehouse, options);
			return warehouse;

		}

	}

	public Warehouse updateWarehouse(RetailscmUserContext userContext,String warehouseId, int warehouseVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingWarehouse(userContext, warehouseId, warehouseVersion, property, newValueExpr, tokensExpr);



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		if(warehouse.getVersion() != warehouseVersion){
			String message = "The target version("+warehouse.getVersion()+") is not equals to version("+warehouseVersion+") provided";
			throwExceptionWithMessage(message);
		}
		synchronized(warehouse){
			//will be good when the warehouse loaded from this JVM process cache.
			//also good when there is a ram based DAO implementation
			//make changes to Warehouse.
			warehouse.updateLastUpdateTime(userContext.now());
			warehouse.changeProperty(property, newValueExpr);
			warehouse = saveWarehouse(userContext, warehouse, tokens().done());
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			//return saveWarehouse(userContext, warehouse, tokens().done());
		}

	}

	public Warehouse updateWarehouseProperty(RetailscmUserContext userContext,String warehouseId, int warehouseVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingWarehouse(userContext, warehouseId, warehouseVersion, property, newValueExpr, tokensExpr);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		if(warehouse.getVersion() != warehouseVersion){
			String message = "The target version("+warehouse.getVersion()+") is not equals to version("+warehouseVersion+") provided";
			throwExceptionWithMessage(message);
		}
		synchronized(warehouse){
			//will be good when the warehouse loaded from this JVM process cache.
			//also good when there is a ram based DAO implementation
			//make changes to Warehouse.

			warehouse.changeProperty(property, newValueExpr);
			warehouse.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().done());
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			//return saveWarehouse(userContext, warehouse, tokens().done());
		}

	}
	protected Map<String,Object> emptyOptions(){
		return tokens().done();
	}

	protected WarehouseTokens tokens(){
		return WarehouseTokens.start();
	}
	protected Map<String,Object> parseTokens(String [] tokensExpr){
		return tokens().initWithArray(tokensExpr);
	}
	protected Map<String,Object> allTokens(){
		return WarehouseTokens.all();
	}
	protected Map<String,Object> analyzeTokens(){
		return tokens().allTokens().analyzeAllLists().done();
	}
	protected Map<String,Object> summaryTokens(){
		return tokens().allTokens().done();
	}
	protected Map<String,Object> viewTokens(){
		return tokens().allTokens()
		.sortStorageSpaceListWith(StorageSpace.ID_PROPERTY,sortDesc())
		.sortSmartPalletListWith(SmartPallet.ID_PROPERTY,sortDesc())
		.sortSupplierSpaceListWith(SupplierSpace.ID_PROPERTY,sortDesc())
		.sortReceivingSpaceListWith(ReceivingSpace.ID_PROPERTY,sortDesc())
		.sortShippingSpaceListWith(ShippingSpace.ID_PROPERTY,sortDesc())
		.sortDamageSpaceListWith(DamageSpace.ID_PROPERTY,sortDesc())
		.sortWarehouseAssetListWith(WarehouseAsset.ID_PROPERTY,sortDesc())
		.done();

	}
	protected Map<String,Object> mergedAllTokens(String []tokens){
		return WarehouseTokens.mergeAll(tokens).done();
	}
	
	protected void checkParamsForTransferingAnotherOwner(RetailscmUserContext userContext, String warehouseId, String anotherOwnerId) throws Exception
 	{

 		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
 		checkerOf(userContext).checkIdOfRetailStoreCountryCenter(anotherOwnerId);//check for optional reference

 		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

 	}
 	public Warehouse transferToAnotherOwner(RetailscmUserContext userContext, String warehouseId, String anotherOwnerId) throws Exception
 	{
 		checkParamsForTransferingAnotherOwner(userContext, warehouseId,anotherOwnerId);
 
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//will be good when the warehouse loaded from this JVM process cache.
			//also good when there is a ram based DAO implementation
			RetailStoreCountryCenter owner = loadRetailStoreCountryCenter(userContext, anotherOwnerId, emptyOptions());
			warehouse.updateOwner(owner);
			warehouse.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, emptyOptions());

			return present(userContext,warehouse, allTokens());

		}

 	}

	


	public CandidateRetailStoreCountryCenter requestCandidateOwner(RetailscmUserContext userContext, String ownerClass, String id, String filterKey, int pageNo) throws Exception {

		CandidateRetailStoreCountryCenter result = new CandidateRetailStoreCountryCenter();
		result.setOwnerClass(ownerClass);
		result.setOwnerId(id);
		result.setFilterKey(filterKey==null?"":filterKey.trim());
		result.setPageNo(pageNo);
		result.setValueFieldName("id");
		result.setDisplayFieldName("name");

		pageNo = Math.max(1, pageNo);
		int pageSize = 20;
		//requestCandidateProductForSkuAsOwner
		SmartList<RetailStoreCountryCenter> candidateList = retailStoreCountryCenterDaoOf(userContext).requestCandidateRetailStoreCountryCenterForWarehouse(userContext,ownerClass, id, filterKey, pageNo, pageSize);
		result.setCandidates(candidateList);
		int totalCount = candidateList.getTotalCount();
		result.setTotalPage(Math.max(1, (totalCount + pageSize -1)/pageSize ));
		return result;
	}

 //--------------------------------------------------------------
	

 	protected RetailStoreCountryCenter loadRetailStoreCountryCenter(RetailscmUserContext userContext, String newOwnerId, Map<String,Object> options) throws Exception
 	{
    
 		return retailStoreCountryCenterDaoOf(userContext).load(newOwnerId, options);
 	  
 	}
 	


	
	//--------------------------------------------------------------

	public void delete(RetailscmUserContext userContext, String warehouseId, int warehouseVersion) throws Exception {
		//deleteInternal(userContext, warehouseId, warehouseVersion);
	}
	protected void deleteInternal(RetailscmUserContext userContext,
			String warehouseId, int warehouseVersion) throws Exception{

		warehouseDaoOf(userContext).delete(warehouseId, warehouseVersion);
	}

	public Warehouse forgetByAll(RetailscmUserContext userContext, String warehouseId, int warehouseVersion) throws Exception {
		return forgetByAllInternal(userContext, warehouseId, warehouseVersion);
	}
	protected Warehouse forgetByAllInternal(RetailscmUserContext userContext,
			String warehouseId, int warehouseVersion) throws Exception{

		return warehouseDaoOf(userContext).disconnectFromAll(warehouseId, warehouseVersion);
	}




	public int deleteAll(RetailscmUserContext userContext, String secureCode) throws Exception
	{
		/*
		if(!("dElEtEaLl".equals(secureCode))){
			throw new WarehouseManagerException("Your secure code is not right, please guess again");
		}
		return deleteAllInternal(userContext);
		*/
		return 0;
	}


	protected int deleteAllInternal(RetailscmUserContext userContext) throws Exception{
		return warehouseDaoOf(userContext).deleteAll();
	}





	protected void checkParamsForAddingStorageSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude,String [] tokensExpr) throws Exception{

				checkerOf(userContext).checkIdOfWarehouse(warehouseId);


		checkerOf(userContext).checkLocationOfStorageSpace(location);

		checkerOf(userContext).checkContactNumberOfStorageSpace(contactNumber);

		checkerOf(userContext).checkTotalAreaOfStorageSpace(totalArea);

		checkerOf(userContext).checkLatitudeOfStorageSpace(latitude);

		checkerOf(userContext).checkLongitudeOfStorageSpace(longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}
	public  Warehouse addStorageSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForAddingStorageSpace(userContext,warehouseId,location, contactNumber, totalArea, latitude, longitude,tokensExpr);

		StorageSpace storageSpace = createStorageSpace(userContext,location, contactNumber, totalArea, latitude, longitude);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, emptyOptions());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.addStorageSpace( storageSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withStorageSpaceList().done());
			
			storageSpaceManagerOf(userContext).onNewInstanceCreated(userContext, storageSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}
	protected void checkParamsForUpdatingStorageSpaceProperties(RetailscmUserContext userContext, String warehouseId,String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude,String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfStorageSpace(id);

		checkerOf(userContext).checkLocationOfStorageSpace( location);
		checkerOf(userContext).checkContactNumberOfStorageSpace( contactNumber);
		checkerOf(userContext).checkTotalAreaOfStorageSpace( totalArea);
		checkerOf(userContext).checkLatitudeOfStorageSpace( latitude);
		checkerOf(userContext).checkLongitudeOfStorageSpace( longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse updateStorageSpaceProperties(RetailscmUserContext userContext, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingStorageSpaceProperties(userContext,warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);

		Map<String, Object> options = tokens()
				.allTokens()
				//.withStorageSpaceListList()
				.searchStorageSpaceListWith(StorageSpace.ID_PROPERTY, tokens().is(), id).done();

		Warehouse warehouseToUpdate = loadWarehouse(userContext, warehouseId, options);

		if(warehouseToUpdate.getStorageSpaceList().isEmpty()){
			throw new WarehouseManagerException("StorageSpace is NOT FOUND with id: '"+id+"'");
		}

		StorageSpace item = warehouseToUpdate.getStorageSpaceList().first();
		beforeUpdateStorageSpaceProperties(userContext,item, warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);
		item.updateLocation( location );
		item.updateContactNumber( contactNumber );
		item.updateTotalArea( totalArea );
		item.updateLatitude( latitude );
		item.updateLongitude( longitude );


		//checkParamsForAddingStorageSpace(userContext,warehouseId,name, code, used,tokensExpr);
		Warehouse warehouse = saveWarehouse(userContext, warehouseToUpdate, tokens().withStorageSpaceList().done());
		synchronized(warehouse){
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}

	protected  void beforeUpdateStorageSpaceProperties(RetailscmUserContext userContext, StorageSpace item, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr)
						throws Exception {
			// by default, nothing to do
	}

	protected StorageSpace createStorageSpace(RetailscmUserContext userContext, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude) throws Exception{

		StorageSpace storageSpace = new StorageSpace();
		
		
		storageSpace.setLocation(location);		
		storageSpace.setContactNumber(contactNumber);		
		storageSpace.setTotalArea(totalArea);		
		storageSpace.setLatitude(latitude);		
		storageSpace.setLongitude(longitude);		
		storageSpace.setLastUpdateTime(userContext.now());
	
		
		return storageSpace;


	}

	protected StorageSpace createIndexedStorageSpace(String id, int version){

		StorageSpace storageSpace = new StorageSpace();
		storageSpace.setId(id);
		storageSpace.setVersion(version);
		return storageSpace;

	}

	protected void checkParamsForRemovingStorageSpaceList(RetailscmUserContext userContext, String warehouseId,
			String storageSpaceIds[],String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		for(String storageSpaceIdItem: storageSpaceIds){
			checkerOf(userContext).checkIdOfStorageSpace(storageSpaceIdItem);
		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

	}
	public  Warehouse removeStorageSpaceList(RetailscmUserContext userContext, String warehouseId,
			String storageSpaceIds[],String [] tokensExpr) throws Exception{

			checkParamsForRemovingStorageSpaceList(userContext, warehouseId,  storageSpaceIds, tokensExpr);


			Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
			synchronized(warehouse){
				//Will be good when the warehouse loaded from this JVM process cache.
				//Also good when there is a RAM based DAO implementation
				warehouseDaoOf(userContext).planToRemoveStorageSpaceList(warehouse, storageSpaceIds, allTokens());
				warehouse = saveWarehouse(userContext, warehouse, tokens().withStorageSpaceList().done());
				deleteRelationListInGraph(userContext, warehouse.getStorageSpaceList());
				return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			}
	}

	protected void checkParamsForRemovingStorageSpace(RetailscmUserContext userContext, String warehouseId,
		String storageSpaceId, int storageSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfStorageSpace(storageSpaceId);
		checkerOf(userContext).checkVersionOfStorageSpace(storageSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse removeStorageSpace(RetailscmUserContext userContext, String warehouseId,
		String storageSpaceId, int storageSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForRemovingStorageSpace(userContext,warehouseId, storageSpaceId, storageSpaceVersion,tokensExpr);

		StorageSpace storageSpace = createIndexedStorageSpace(storageSpaceId, storageSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.removeStorageSpace( storageSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withStorageSpaceList().done());
			deleteRelationInGraph(userContext, storageSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}


	}
	protected void checkParamsForCopyingStorageSpace(RetailscmUserContext userContext, String warehouseId,
		String storageSpaceId, int storageSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfStorageSpace(storageSpaceId);
		checkerOf(userContext).checkVersionOfStorageSpace(storageSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse copyStorageSpaceFrom(RetailscmUserContext userContext, String warehouseId,
		String storageSpaceId, int storageSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForCopyingStorageSpace(userContext,warehouseId, storageSpaceId, storageSpaceVersion,tokensExpr);

		StorageSpace storageSpace = createIndexedStorageSpace(storageSpaceId, storageSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation

			storageSpace.updateLastUpdateTime(userContext.now());

			warehouse.copyStorageSpaceFrom( storageSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withStorageSpaceList().done());
			
			storageSpaceManagerOf(userContext).onNewInstanceCreated(userContext, (StorageSpace)warehouse.getFlexiableObjects().get(BaseEntity.COPIED_CHILD));
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	protected void checkParamsForUpdatingStorageSpace(RetailscmUserContext userContext, String warehouseId, String storageSpaceId, int storageSpaceVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception{
		


		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfStorageSpace(storageSpaceId);
		checkerOf(userContext).checkVersionOfStorageSpace(storageSpaceVersion);


		if(StorageSpace.LOCATION_PROPERTY.equals(property)){
			checkerOf(userContext).checkLocationOfStorageSpace(parseString(newValueExpr));
		}
		
		if(StorageSpace.CONTACT_NUMBER_PROPERTY.equals(property)){
			checkerOf(userContext).checkContactNumberOfStorageSpace(parseString(newValueExpr));
		}
		
		if(StorageSpace.TOTAL_AREA_PROPERTY.equals(property)){
			checkerOf(userContext).checkTotalAreaOfStorageSpace(parseString(newValueExpr));
		}
		
		if(StorageSpace.LATITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLatitudeOfStorageSpace(parseBigDecimal(newValueExpr));
		}
		
		if(StorageSpace.LONGITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLongitudeOfStorageSpace(parseBigDecimal(newValueExpr));
		}
		


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}

	public  Warehouse updateStorageSpace(RetailscmUserContext userContext, String warehouseId, String storageSpaceId, int storageSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
			throws Exception{

		checkParamsForUpdatingStorageSpace(userContext, warehouseId, storageSpaceId, storageSpaceVersion, property, newValueExpr,  tokensExpr);

		Map<String,Object> loadTokens = this.tokens().withStorageSpaceList().searchStorageSpaceListWith(StorageSpace.ID_PROPERTY, tokens().equals(), storageSpaceId).done();



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, loadTokens);

		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			//warehouse.removeStorageSpace( storageSpace );
			//make changes to AcceleraterAccount.
			StorageSpace storageSpaceIdVersionKey = createIndexedStorageSpace(storageSpaceId, storageSpaceVersion);

			StorageSpace storageSpace = warehouse.findTheStorageSpace(storageSpaceIdVersionKey);
			if(storageSpace == null){
				throw new WarehouseManagerException(storageSpaceId+" is NOT FOUND" );
			}

			beforeUpdateStorageSpace(userContext, storageSpace, warehouseId, storageSpaceId, storageSpaceVersion, property, newValueExpr,  tokensExpr);
			storageSpace.changeProperty(property, newValueExpr);
			storageSpace.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().withStorageSpaceList().done());
			storageSpaceManagerOf(userContext).onUpdated(userContext, storageSpace, this, "updateStorageSpace");
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	/** if you has something need to do before update data from DB, override this */
	protected void beforeUpdateStorageSpace(RetailscmUserContext userContext, StorageSpace existed, String warehouseId, String storageSpaceId, int storageSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
  			throws Exception{
  }
	/*

	*/




	protected void checkParamsForAddingSmartPallet(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude,String [] tokensExpr) throws Exception{

				checkerOf(userContext).checkIdOfWarehouse(warehouseId);


		checkerOf(userContext).checkLocationOfSmartPallet(location);

		checkerOf(userContext).checkContactNumberOfSmartPallet(contactNumber);

		checkerOf(userContext).checkTotalAreaOfSmartPallet(totalArea);

		checkerOf(userContext).checkLatitudeOfSmartPallet(latitude);

		checkerOf(userContext).checkLongitudeOfSmartPallet(longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}
	public  Warehouse addSmartPallet(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForAddingSmartPallet(userContext,warehouseId,location, contactNumber, totalArea, latitude, longitude,tokensExpr);

		SmartPallet smartPallet = createSmartPallet(userContext,location, contactNumber, totalArea, latitude, longitude);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, emptyOptions());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.addSmartPallet( smartPallet );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSmartPalletList().done());
			
			smartPalletManagerOf(userContext).onNewInstanceCreated(userContext, smartPallet);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}
	protected void checkParamsForUpdatingSmartPalletProperties(RetailscmUserContext userContext, String warehouseId,String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude,String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfSmartPallet(id);

		checkerOf(userContext).checkLocationOfSmartPallet( location);
		checkerOf(userContext).checkContactNumberOfSmartPallet( contactNumber);
		checkerOf(userContext).checkTotalAreaOfSmartPallet( totalArea);
		checkerOf(userContext).checkLatitudeOfSmartPallet( latitude);
		checkerOf(userContext).checkLongitudeOfSmartPallet( longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse updateSmartPalletProperties(RetailscmUserContext userContext, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingSmartPalletProperties(userContext,warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);

		Map<String, Object> options = tokens()
				.allTokens()
				//.withSmartPalletListList()
				.searchSmartPalletListWith(SmartPallet.ID_PROPERTY, tokens().is(), id).done();

		Warehouse warehouseToUpdate = loadWarehouse(userContext, warehouseId, options);

		if(warehouseToUpdate.getSmartPalletList().isEmpty()){
			throw new WarehouseManagerException("SmartPallet is NOT FOUND with id: '"+id+"'");
		}

		SmartPallet item = warehouseToUpdate.getSmartPalletList().first();
		beforeUpdateSmartPalletProperties(userContext,item, warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);
		item.updateLocation( location );
		item.updateContactNumber( contactNumber );
		item.updateTotalArea( totalArea );
		item.updateLatitude( latitude );
		item.updateLongitude( longitude );


		//checkParamsForAddingSmartPallet(userContext,warehouseId,name, code, used,tokensExpr);
		Warehouse warehouse = saveWarehouse(userContext, warehouseToUpdate, tokens().withSmartPalletList().done());
		synchronized(warehouse){
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}

	protected  void beforeUpdateSmartPalletProperties(RetailscmUserContext userContext, SmartPallet item, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr)
						throws Exception {
			// by default, nothing to do
	}

	protected SmartPallet createSmartPallet(RetailscmUserContext userContext, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude) throws Exception{

		SmartPallet smartPallet = new SmartPallet();
		
		
		smartPallet.setLocation(location);		
		smartPallet.setContactNumber(contactNumber);		
		smartPallet.setTotalArea(totalArea);		
		smartPallet.setLatitude(latitude);		
		smartPallet.setLongitude(longitude);		
		smartPallet.setLastUpdateTime(userContext.now());
	
		
		return smartPallet;


	}

	protected SmartPallet createIndexedSmartPallet(String id, int version){

		SmartPallet smartPallet = new SmartPallet();
		smartPallet.setId(id);
		smartPallet.setVersion(version);
		return smartPallet;

	}

	protected void checkParamsForRemovingSmartPalletList(RetailscmUserContext userContext, String warehouseId,
			String smartPalletIds[],String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		for(String smartPalletIdItem: smartPalletIds){
			checkerOf(userContext).checkIdOfSmartPallet(smartPalletIdItem);
		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

	}
	public  Warehouse removeSmartPalletList(RetailscmUserContext userContext, String warehouseId,
			String smartPalletIds[],String [] tokensExpr) throws Exception{

			checkParamsForRemovingSmartPalletList(userContext, warehouseId,  smartPalletIds, tokensExpr);


			Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
			synchronized(warehouse){
				//Will be good when the warehouse loaded from this JVM process cache.
				//Also good when there is a RAM based DAO implementation
				warehouseDaoOf(userContext).planToRemoveSmartPalletList(warehouse, smartPalletIds, allTokens());
				warehouse = saveWarehouse(userContext, warehouse, tokens().withSmartPalletList().done());
				deleteRelationListInGraph(userContext, warehouse.getSmartPalletList());
				return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			}
	}

	protected void checkParamsForRemovingSmartPallet(RetailscmUserContext userContext, String warehouseId,
		String smartPalletId, int smartPalletVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfSmartPallet(smartPalletId);
		checkerOf(userContext).checkVersionOfSmartPallet(smartPalletVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse removeSmartPallet(RetailscmUserContext userContext, String warehouseId,
		String smartPalletId, int smartPalletVersion,String [] tokensExpr) throws Exception{

		checkParamsForRemovingSmartPallet(userContext,warehouseId, smartPalletId, smartPalletVersion,tokensExpr);

		SmartPallet smartPallet = createIndexedSmartPallet(smartPalletId, smartPalletVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.removeSmartPallet( smartPallet );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSmartPalletList().done());
			deleteRelationInGraph(userContext, smartPallet);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}


	}
	protected void checkParamsForCopyingSmartPallet(RetailscmUserContext userContext, String warehouseId,
		String smartPalletId, int smartPalletVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfSmartPallet(smartPalletId);
		checkerOf(userContext).checkVersionOfSmartPallet(smartPalletVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse copySmartPalletFrom(RetailscmUserContext userContext, String warehouseId,
		String smartPalletId, int smartPalletVersion,String [] tokensExpr) throws Exception{

		checkParamsForCopyingSmartPallet(userContext,warehouseId, smartPalletId, smartPalletVersion,tokensExpr);

		SmartPallet smartPallet = createIndexedSmartPallet(smartPalletId, smartPalletVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation

			smartPallet.updateLastUpdateTime(userContext.now());

			warehouse.copySmartPalletFrom( smartPallet );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSmartPalletList().done());
			
			smartPalletManagerOf(userContext).onNewInstanceCreated(userContext, (SmartPallet)warehouse.getFlexiableObjects().get(BaseEntity.COPIED_CHILD));
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	protected void checkParamsForUpdatingSmartPallet(RetailscmUserContext userContext, String warehouseId, String smartPalletId, int smartPalletVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception{
		


		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfSmartPallet(smartPalletId);
		checkerOf(userContext).checkVersionOfSmartPallet(smartPalletVersion);


		if(SmartPallet.LOCATION_PROPERTY.equals(property)){
			checkerOf(userContext).checkLocationOfSmartPallet(parseString(newValueExpr));
		}
		
		if(SmartPallet.CONTACT_NUMBER_PROPERTY.equals(property)){
			checkerOf(userContext).checkContactNumberOfSmartPallet(parseString(newValueExpr));
		}
		
		if(SmartPallet.TOTAL_AREA_PROPERTY.equals(property)){
			checkerOf(userContext).checkTotalAreaOfSmartPallet(parseString(newValueExpr));
		}
		
		if(SmartPallet.LATITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLatitudeOfSmartPallet(parseBigDecimal(newValueExpr));
		}
		
		if(SmartPallet.LONGITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLongitudeOfSmartPallet(parseBigDecimal(newValueExpr));
		}
		


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}

	public  Warehouse updateSmartPallet(RetailscmUserContext userContext, String warehouseId, String smartPalletId, int smartPalletVersion, String property, String newValueExpr,String [] tokensExpr)
			throws Exception{

		checkParamsForUpdatingSmartPallet(userContext, warehouseId, smartPalletId, smartPalletVersion, property, newValueExpr,  tokensExpr);

		Map<String,Object> loadTokens = this.tokens().withSmartPalletList().searchSmartPalletListWith(SmartPallet.ID_PROPERTY, tokens().equals(), smartPalletId).done();



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, loadTokens);

		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			//warehouse.removeSmartPallet( smartPallet );
			//make changes to AcceleraterAccount.
			SmartPallet smartPalletIdVersionKey = createIndexedSmartPallet(smartPalletId, smartPalletVersion);

			SmartPallet smartPallet = warehouse.findTheSmartPallet(smartPalletIdVersionKey);
			if(smartPallet == null){
				throw new WarehouseManagerException(smartPalletId+" is NOT FOUND" );
			}

			beforeUpdateSmartPallet(userContext, smartPallet, warehouseId, smartPalletId, smartPalletVersion, property, newValueExpr,  tokensExpr);
			smartPallet.changeProperty(property, newValueExpr);
			smartPallet.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSmartPalletList().done());
			smartPalletManagerOf(userContext).onUpdated(userContext, smartPallet, this, "updateSmartPallet");
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	/** if you has something need to do before update data from DB, override this */
	protected void beforeUpdateSmartPallet(RetailscmUserContext userContext, SmartPallet existed, String warehouseId, String smartPalletId, int smartPalletVersion, String property, String newValueExpr,String [] tokensExpr)
  			throws Exception{
  }
	/*

	*/




	protected void checkParamsForAddingSupplierSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude,String [] tokensExpr) throws Exception{

				checkerOf(userContext).checkIdOfWarehouse(warehouseId);


		checkerOf(userContext).checkLocationOfSupplierSpace(location);

		checkerOf(userContext).checkContactNumberOfSupplierSpace(contactNumber);

		checkerOf(userContext).checkTotalAreaOfSupplierSpace(totalArea);

		checkerOf(userContext).checkLatitudeOfSupplierSpace(latitude);

		checkerOf(userContext).checkLongitudeOfSupplierSpace(longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}
	public  Warehouse addSupplierSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForAddingSupplierSpace(userContext,warehouseId,location, contactNumber, totalArea, latitude, longitude,tokensExpr);

		SupplierSpace supplierSpace = createSupplierSpace(userContext,location, contactNumber, totalArea, latitude, longitude);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, emptyOptions());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.addSupplierSpace( supplierSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSupplierSpaceList().done());
			
			supplierSpaceManagerOf(userContext).onNewInstanceCreated(userContext, supplierSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}
	protected void checkParamsForUpdatingSupplierSpaceProperties(RetailscmUserContext userContext, String warehouseId,String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude,String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfSupplierSpace(id);

		checkerOf(userContext).checkLocationOfSupplierSpace( location);
		checkerOf(userContext).checkContactNumberOfSupplierSpace( contactNumber);
		checkerOf(userContext).checkTotalAreaOfSupplierSpace( totalArea);
		checkerOf(userContext).checkLatitudeOfSupplierSpace( latitude);
		checkerOf(userContext).checkLongitudeOfSupplierSpace( longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse updateSupplierSpaceProperties(RetailscmUserContext userContext, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingSupplierSpaceProperties(userContext,warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);

		Map<String, Object> options = tokens()
				.allTokens()
				//.withSupplierSpaceListList()
				.searchSupplierSpaceListWith(SupplierSpace.ID_PROPERTY, tokens().is(), id).done();

		Warehouse warehouseToUpdate = loadWarehouse(userContext, warehouseId, options);

		if(warehouseToUpdate.getSupplierSpaceList().isEmpty()){
			throw new WarehouseManagerException("SupplierSpace is NOT FOUND with id: '"+id+"'");
		}

		SupplierSpace item = warehouseToUpdate.getSupplierSpaceList().first();
		beforeUpdateSupplierSpaceProperties(userContext,item, warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);
		item.updateLocation( location );
		item.updateContactNumber( contactNumber );
		item.updateTotalArea( totalArea );
		item.updateLatitude( latitude );
		item.updateLongitude( longitude );


		//checkParamsForAddingSupplierSpace(userContext,warehouseId,name, code, used,tokensExpr);
		Warehouse warehouse = saveWarehouse(userContext, warehouseToUpdate, tokens().withSupplierSpaceList().done());
		synchronized(warehouse){
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}

	protected  void beforeUpdateSupplierSpaceProperties(RetailscmUserContext userContext, SupplierSpace item, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr)
						throws Exception {
			// by default, nothing to do
	}

	protected SupplierSpace createSupplierSpace(RetailscmUserContext userContext, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude) throws Exception{

		SupplierSpace supplierSpace = new SupplierSpace();
		
		
		supplierSpace.setLocation(location);		
		supplierSpace.setContactNumber(contactNumber);		
		supplierSpace.setTotalArea(totalArea);		
		supplierSpace.setLatitude(latitude);		
		supplierSpace.setLongitude(longitude);		
		supplierSpace.setLastUpdateTime(userContext.now());
	
		
		return supplierSpace;


	}

	protected SupplierSpace createIndexedSupplierSpace(String id, int version){

		SupplierSpace supplierSpace = new SupplierSpace();
		supplierSpace.setId(id);
		supplierSpace.setVersion(version);
		return supplierSpace;

	}

	protected void checkParamsForRemovingSupplierSpaceList(RetailscmUserContext userContext, String warehouseId,
			String supplierSpaceIds[],String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		for(String supplierSpaceIdItem: supplierSpaceIds){
			checkerOf(userContext).checkIdOfSupplierSpace(supplierSpaceIdItem);
		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

	}
	public  Warehouse removeSupplierSpaceList(RetailscmUserContext userContext, String warehouseId,
			String supplierSpaceIds[],String [] tokensExpr) throws Exception{

			checkParamsForRemovingSupplierSpaceList(userContext, warehouseId,  supplierSpaceIds, tokensExpr);


			Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
			synchronized(warehouse){
				//Will be good when the warehouse loaded from this JVM process cache.
				//Also good when there is a RAM based DAO implementation
				warehouseDaoOf(userContext).planToRemoveSupplierSpaceList(warehouse, supplierSpaceIds, allTokens());
				warehouse = saveWarehouse(userContext, warehouse, tokens().withSupplierSpaceList().done());
				deleteRelationListInGraph(userContext, warehouse.getSupplierSpaceList());
				return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			}
	}

	protected void checkParamsForRemovingSupplierSpace(RetailscmUserContext userContext, String warehouseId,
		String supplierSpaceId, int supplierSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfSupplierSpace(supplierSpaceId);
		checkerOf(userContext).checkVersionOfSupplierSpace(supplierSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse removeSupplierSpace(RetailscmUserContext userContext, String warehouseId,
		String supplierSpaceId, int supplierSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForRemovingSupplierSpace(userContext,warehouseId, supplierSpaceId, supplierSpaceVersion,tokensExpr);

		SupplierSpace supplierSpace = createIndexedSupplierSpace(supplierSpaceId, supplierSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.removeSupplierSpace( supplierSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSupplierSpaceList().done());
			deleteRelationInGraph(userContext, supplierSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}


	}
	protected void checkParamsForCopyingSupplierSpace(RetailscmUserContext userContext, String warehouseId,
		String supplierSpaceId, int supplierSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfSupplierSpace(supplierSpaceId);
		checkerOf(userContext).checkVersionOfSupplierSpace(supplierSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse copySupplierSpaceFrom(RetailscmUserContext userContext, String warehouseId,
		String supplierSpaceId, int supplierSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForCopyingSupplierSpace(userContext,warehouseId, supplierSpaceId, supplierSpaceVersion,tokensExpr);

		SupplierSpace supplierSpace = createIndexedSupplierSpace(supplierSpaceId, supplierSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation

			supplierSpace.updateLastUpdateTime(userContext.now());

			warehouse.copySupplierSpaceFrom( supplierSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSupplierSpaceList().done());
			
			supplierSpaceManagerOf(userContext).onNewInstanceCreated(userContext, (SupplierSpace)warehouse.getFlexiableObjects().get(BaseEntity.COPIED_CHILD));
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	protected void checkParamsForUpdatingSupplierSpace(RetailscmUserContext userContext, String warehouseId, String supplierSpaceId, int supplierSpaceVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception{
		


		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfSupplierSpace(supplierSpaceId);
		checkerOf(userContext).checkVersionOfSupplierSpace(supplierSpaceVersion);


		if(SupplierSpace.LOCATION_PROPERTY.equals(property)){
			checkerOf(userContext).checkLocationOfSupplierSpace(parseString(newValueExpr));
		}
		
		if(SupplierSpace.CONTACT_NUMBER_PROPERTY.equals(property)){
			checkerOf(userContext).checkContactNumberOfSupplierSpace(parseString(newValueExpr));
		}
		
		if(SupplierSpace.TOTAL_AREA_PROPERTY.equals(property)){
			checkerOf(userContext).checkTotalAreaOfSupplierSpace(parseString(newValueExpr));
		}
		
		if(SupplierSpace.LATITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLatitudeOfSupplierSpace(parseBigDecimal(newValueExpr));
		}
		
		if(SupplierSpace.LONGITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLongitudeOfSupplierSpace(parseBigDecimal(newValueExpr));
		}
		


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}

	public  Warehouse updateSupplierSpace(RetailscmUserContext userContext, String warehouseId, String supplierSpaceId, int supplierSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
			throws Exception{

		checkParamsForUpdatingSupplierSpace(userContext, warehouseId, supplierSpaceId, supplierSpaceVersion, property, newValueExpr,  tokensExpr);

		Map<String,Object> loadTokens = this.tokens().withSupplierSpaceList().searchSupplierSpaceListWith(SupplierSpace.ID_PROPERTY, tokens().equals(), supplierSpaceId).done();



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, loadTokens);

		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			//warehouse.removeSupplierSpace( supplierSpace );
			//make changes to AcceleraterAccount.
			SupplierSpace supplierSpaceIdVersionKey = createIndexedSupplierSpace(supplierSpaceId, supplierSpaceVersion);

			SupplierSpace supplierSpace = warehouse.findTheSupplierSpace(supplierSpaceIdVersionKey);
			if(supplierSpace == null){
				throw new WarehouseManagerException(supplierSpaceId+" is NOT FOUND" );
			}

			beforeUpdateSupplierSpace(userContext, supplierSpace, warehouseId, supplierSpaceId, supplierSpaceVersion, property, newValueExpr,  tokensExpr);
			supplierSpace.changeProperty(property, newValueExpr);
			supplierSpace.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().withSupplierSpaceList().done());
			supplierSpaceManagerOf(userContext).onUpdated(userContext, supplierSpace, this, "updateSupplierSpace");
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	/** if you has something need to do before update data from DB, override this */
	protected void beforeUpdateSupplierSpace(RetailscmUserContext userContext, SupplierSpace existed, String warehouseId, String supplierSpaceId, int supplierSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
  			throws Exception{
  }
	/*

	*/




	protected void checkParamsForAddingReceivingSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String description, String totalArea, BigDecimal latitude, BigDecimal longitude,String [] tokensExpr) throws Exception{

				checkerOf(userContext).checkIdOfWarehouse(warehouseId);


		checkerOf(userContext).checkLocationOfReceivingSpace(location);

		checkerOf(userContext).checkContactNumberOfReceivingSpace(contactNumber);

		checkerOf(userContext).checkDescriptionOfReceivingSpace(description);

		checkerOf(userContext).checkTotalAreaOfReceivingSpace(totalArea);

		checkerOf(userContext).checkLatitudeOfReceivingSpace(latitude);

		checkerOf(userContext).checkLongitudeOfReceivingSpace(longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}
	public  Warehouse addReceivingSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String description, String totalArea, BigDecimal latitude, BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForAddingReceivingSpace(userContext,warehouseId,location, contactNumber, description, totalArea, latitude, longitude,tokensExpr);

		ReceivingSpace receivingSpace = createReceivingSpace(userContext,location, contactNumber, description, totalArea, latitude, longitude);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, emptyOptions());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.addReceivingSpace( receivingSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withReceivingSpaceList().done());
			
			receivingSpaceManagerOf(userContext).onNewInstanceCreated(userContext, receivingSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}
	protected void checkParamsForUpdatingReceivingSpaceProperties(RetailscmUserContext userContext, String warehouseId,String id,String location,String contactNumber,String description,String totalArea,BigDecimal latitude,BigDecimal longitude,String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfReceivingSpace(id);

		checkerOf(userContext).checkLocationOfReceivingSpace( location);
		checkerOf(userContext).checkContactNumberOfReceivingSpace( contactNumber);
		checkerOf(userContext).checkDescriptionOfReceivingSpace( description);
		checkerOf(userContext).checkTotalAreaOfReceivingSpace( totalArea);
		checkerOf(userContext).checkLatitudeOfReceivingSpace( latitude);
		checkerOf(userContext).checkLongitudeOfReceivingSpace( longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse updateReceivingSpaceProperties(RetailscmUserContext userContext, String warehouseId, String id,String location,String contactNumber,String description,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingReceivingSpaceProperties(userContext,warehouseId,id,location,contactNumber,description,totalArea,latitude,longitude,tokensExpr);

		Map<String, Object> options = tokens()
				.allTokens()
				//.withReceivingSpaceListList()
				.searchReceivingSpaceListWith(ReceivingSpace.ID_PROPERTY, tokens().is(), id).done();

		Warehouse warehouseToUpdate = loadWarehouse(userContext, warehouseId, options);

		if(warehouseToUpdate.getReceivingSpaceList().isEmpty()){
			throw new WarehouseManagerException("ReceivingSpace is NOT FOUND with id: '"+id+"'");
		}

		ReceivingSpace item = warehouseToUpdate.getReceivingSpaceList().first();
		beforeUpdateReceivingSpaceProperties(userContext,item, warehouseId,id,location,contactNumber,description,totalArea,latitude,longitude,tokensExpr);
		item.updateLocation( location );
		item.updateContactNumber( contactNumber );
		item.updateDescription( description );
		item.updateTotalArea( totalArea );
		item.updateLatitude( latitude );
		item.updateLongitude( longitude );


		//checkParamsForAddingReceivingSpace(userContext,warehouseId,name, code, used,tokensExpr);
		Warehouse warehouse = saveWarehouse(userContext, warehouseToUpdate, tokens().withReceivingSpaceList().done());
		synchronized(warehouse){
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}

	protected  void beforeUpdateReceivingSpaceProperties(RetailscmUserContext userContext, ReceivingSpace item, String warehouseId, String id,String location,String contactNumber,String description,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr)
						throws Exception {
			// by default, nothing to do
	}

	protected ReceivingSpace createReceivingSpace(RetailscmUserContext userContext, String location, String contactNumber, String description, String totalArea, BigDecimal latitude, BigDecimal longitude) throws Exception{

		ReceivingSpace receivingSpace = new ReceivingSpace();
		
		
		receivingSpace.setLocation(location);		
		receivingSpace.setContactNumber(contactNumber);		
		receivingSpace.setDescription(description);		
		receivingSpace.setTotalArea(totalArea);		
		receivingSpace.setLatitude(latitude);		
		receivingSpace.setLongitude(longitude);		
		receivingSpace.setLastUpdateTime(userContext.now());
	
		
		return receivingSpace;


	}

	protected ReceivingSpace createIndexedReceivingSpace(String id, int version){

		ReceivingSpace receivingSpace = new ReceivingSpace();
		receivingSpace.setId(id);
		receivingSpace.setVersion(version);
		return receivingSpace;

	}

	protected void checkParamsForRemovingReceivingSpaceList(RetailscmUserContext userContext, String warehouseId,
			String receivingSpaceIds[],String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		for(String receivingSpaceIdItem: receivingSpaceIds){
			checkerOf(userContext).checkIdOfReceivingSpace(receivingSpaceIdItem);
		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

	}
	public  Warehouse removeReceivingSpaceList(RetailscmUserContext userContext, String warehouseId,
			String receivingSpaceIds[],String [] tokensExpr) throws Exception{

			checkParamsForRemovingReceivingSpaceList(userContext, warehouseId,  receivingSpaceIds, tokensExpr);


			Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
			synchronized(warehouse){
				//Will be good when the warehouse loaded from this JVM process cache.
				//Also good when there is a RAM based DAO implementation
				warehouseDaoOf(userContext).planToRemoveReceivingSpaceList(warehouse, receivingSpaceIds, allTokens());
				warehouse = saveWarehouse(userContext, warehouse, tokens().withReceivingSpaceList().done());
				deleteRelationListInGraph(userContext, warehouse.getReceivingSpaceList());
				return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			}
	}

	protected void checkParamsForRemovingReceivingSpace(RetailscmUserContext userContext, String warehouseId,
		String receivingSpaceId, int receivingSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfReceivingSpace(receivingSpaceId);
		checkerOf(userContext).checkVersionOfReceivingSpace(receivingSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse removeReceivingSpace(RetailscmUserContext userContext, String warehouseId,
		String receivingSpaceId, int receivingSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForRemovingReceivingSpace(userContext,warehouseId, receivingSpaceId, receivingSpaceVersion,tokensExpr);

		ReceivingSpace receivingSpace = createIndexedReceivingSpace(receivingSpaceId, receivingSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.removeReceivingSpace( receivingSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withReceivingSpaceList().done());
			deleteRelationInGraph(userContext, receivingSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}


	}
	protected void checkParamsForCopyingReceivingSpace(RetailscmUserContext userContext, String warehouseId,
		String receivingSpaceId, int receivingSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfReceivingSpace(receivingSpaceId);
		checkerOf(userContext).checkVersionOfReceivingSpace(receivingSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse copyReceivingSpaceFrom(RetailscmUserContext userContext, String warehouseId,
		String receivingSpaceId, int receivingSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForCopyingReceivingSpace(userContext,warehouseId, receivingSpaceId, receivingSpaceVersion,tokensExpr);

		ReceivingSpace receivingSpace = createIndexedReceivingSpace(receivingSpaceId, receivingSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation

			receivingSpace.updateLastUpdateTime(userContext.now());

			warehouse.copyReceivingSpaceFrom( receivingSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withReceivingSpaceList().done());
			
			receivingSpaceManagerOf(userContext).onNewInstanceCreated(userContext, (ReceivingSpace)warehouse.getFlexiableObjects().get(BaseEntity.COPIED_CHILD));
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	protected void checkParamsForUpdatingReceivingSpace(RetailscmUserContext userContext, String warehouseId, String receivingSpaceId, int receivingSpaceVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception{
		


		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfReceivingSpace(receivingSpaceId);
		checkerOf(userContext).checkVersionOfReceivingSpace(receivingSpaceVersion);


		if(ReceivingSpace.LOCATION_PROPERTY.equals(property)){
			checkerOf(userContext).checkLocationOfReceivingSpace(parseString(newValueExpr));
		}
		
		if(ReceivingSpace.CONTACT_NUMBER_PROPERTY.equals(property)){
			checkerOf(userContext).checkContactNumberOfReceivingSpace(parseString(newValueExpr));
		}
		
		if(ReceivingSpace.DESCRIPTION_PROPERTY.equals(property)){
			checkerOf(userContext).checkDescriptionOfReceivingSpace(parseString(newValueExpr));
		}
		
		if(ReceivingSpace.TOTAL_AREA_PROPERTY.equals(property)){
			checkerOf(userContext).checkTotalAreaOfReceivingSpace(parseString(newValueExpr));
		}
		
		if(ReceivingSpace.LATITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLatitudeOfReceivingSpace(parseBigDecimal(newValueExpr));
		}
		
		if(ReceivingSpace.LONGITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLongitudeOfReceivingSpace(parseBigDecimal(newValueExpr));
		}
		


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}

	public  Warehouse updateReceivingSpace(RetailscmUserContext userContext, String warehouseId, String receivingSpaceId, int receivingSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
			throws Exception{

		checkParamsForUpdatingReceivingSpace(userContext, warehouseId, receivingSpaceId, receivingSpaceVersion, property, newValueExpr,  tokensExpr);

		Map<String,Object> loadTokens = this.tokens().withReceivingSpaceList().searchReceivingSpaceListWith(ReceivingSpace.ID_PROPERTY, tokens().equals(), receivingSpaceId).done();



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, loadTokens);

		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			//warehouse.removeReceivingSpace( receivingSpace );
			//make changes to AcceleraterAccount.
			ReceivingSpace receivingSpaceIdVersionKey = createIndexedReceivingSpace(receivingSpaceId, receivingSpaceVersion);

			ReceivingSpace receivingSpace = warehouse.findTheReceivingSpace(receivingSpaceIdVersionKey);
			if(receivingSpace == null){
				throw new WarehouseManagerException(receivingSpaceId+" is NOT FOUND" );
			}

			beforeUpdateReceivingSpace(userContext, receivingSpace, warehouseId, receivingSpaceId, receivingSpaceVersion, property, newValueExpr,  tokensExpr);
			receivingSpace.changeProperty(property, newValueExpr);
			receivingSpace.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().withReceivingSpaceList().done());
			receivingSpaceManagerOf(userContext).onUpdated(userContext, receivingSpace, this, "updateReceivingSpace");
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	/** if you has something need to do before update data from DB, override this */
	protected void beforeUpdateReceivingSpace(RetailscmUserContext userContext, ReceivingSpace existed, String warehouseId, String receivingSpaceId, int receivingSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
  			throws Exception{
  }
	/*

	*/




	protected void checkParamsForAddingShippingSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude, String description,String [] tokensExpr) throws Exception{

				checkerOf(userContext).checkIdOfWarehouse(warehouseId);


		checkerOf(userContext).checkLocationOfShippingSpace(location);

		checkerOf(userContext).checkContactNumberOfShippingSpace(contactNumber);

		checkerOf(userContext).checkTotalAreaOfShippingSpace(totalArea);

		checkerOf(userContext).checkLatitudeOfShippingSpace(latitude);

		checkerOf(userContext).checkLongitudeOfShippingSpace(longitude);

		checkerOf(userContext).checkDescriptionOfShippingSpace(description);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}
	public  Warehouse addShippingSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude, String description, String [] tokensExpr) throws Exception
	{
		checkParamsForAddingShippingSpace(userContext,warehouseId,location, contactNumber, totalArea, latitude, longitude, description,tokensExpr);

		ShippingSpace shippingSpace = createShippingSpace(userContext,location, contactNumber, totalArea, latitude, longitude, description);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, emptyOptions());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.addShippingSpace( shippingSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withShippingSpaceList().done());
			
			shippingSpaceManagerOf(userContext).onNewInstanceCreated(userContext, shippingSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}
	protected void checkParamsForUpdatingShippingSpaceProperties(RetailscmUserContext userContext, String warehouseId,String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude,String description,String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfShippingSpace(id);

		checkerOf(userContext).checkLocationOfShippingSpace( location);
		checkerOf(userContext).checkContactNumberOfShippingSpace( contactNumber);
		checkerOf(userContext).checkTotalAreaOfShippingSpace( totalArea);
		checkerOf(userContext).checkLatitudeOfShippingSpace( latitude);
		checkerOf(userContext).checkLongitudeOfShippingSpace( longitude);
		checkerOf(userContext).checkDescriptionOfShippingSpace( description);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse updateShippingSpaceProperties(RetailscmUserContext userContext, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude,String description, String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingShippingSpaceProperties(userContext,warehouseId,id,location,contactNumber,totalArea,latitude,longitude,description,tokensExpr);

		Map<String, Object> options = tokens()
				.allTokens()
				//.withShippingSpaceListList()
				.searchShippingSpaceListWith(ShippingSpace.ID_PROPERTY, tokens().is(), id).done();

		Warehouse warehouseToUpdate = loadWarehouse(userContext, warehouseId, options);

		if(warehouseToUpdate.getShippingSpaceList().isEmpty()){
			throw new WarehouseManagerException("ShippingSpace is NOT FOUND with id: '"+id+"'");
		}

		ShippingSpace item = warehouseToUpdate.getShippingSpaceList().first();
		beforeUpdateShippingSpaceProperties(userContext,item, warehouseId,id,location,contactNumber,totalArea,latitude,longitude,description,tokensExpr);
		item.updateLocation( location );
		item.updateContactNumber( contactNumber );
		item.updateTotalArea( totalArea );
		item.updateLatitude( latitude );
		item.updateLongitude( longitude );
		item.updateDescription( description );


		//checkParamsForAddingShippingSpace(userContext,warehouseId,name, code, used,tokensExpr);
		Warehouse warehouse = saveWarehouse(userContext, warehouseToUpdate, tokens().withShippingSpaceList().done());
		synchronized(warehouse){
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}

	protected  void beforeUpdateShippingSpaceProperties(RetailscmUserContext userContext, ShippingSpace item, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude,String description, String [] tokensExpr)
						throws Exception {
			// by default, nothing to do
	}

	protected ShippingSpace createShippingSpace(RetailscmUserContext userContext, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude, String description) throws Exception{

		ShippingSpace shippingSpace = new ShippingSpace();
		
		
		shippingSpace.setLocation(location);		
		shippingSpace.setContactNumber(contactNumber);		
		shippingSpace.setTotalArea(totalArea);		
		shippingSpace.setLatitude(latitude);		
		shippingSpace.setLongitude(longitude);		
		shippingSpace.setDescription(description);		
		shippingSpace.setLastUpdateTime(userContext.now());
	
		
		return shippingSpace;


	}

	protected ShippingSpace createIndexedShippingSpace(String id, int version){

		ShippingSpace shippingSpace = new ShippingSpace();
		shippingSpace.setId(id);
		shippingSpace.setVersion(version);
		return shippingSpace;

	}

	protected void checkParamsForRemovingShippingSpaceList(RetailscmUserContext userContext, String warehouseId,
			String shippingSpaceIds[],String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		for(String shippingSpaceIdItem: shippingSpaceIds){
			checkerOf(userContext).checkIdOfShippingSpace(shippingSpaceIdItem);
		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

	}
	public  Warehouse removeShippingSpaceList(RetailscmUserContext userContext, String warehouseId,
			String shippingSpaceIds[],String [] tokensExpr) throws Exception{

			checkParamsForRemovingShippingSpaceList(userContext, warehouseId,  shippingSpaceIds, tokensExpr);


			Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
			synchronized(warehouse){
				//Will be good when the warehouse loaded from this JVM process cache.
				//Also good when there is a RAM based DAO implementation
				warehouseDaoOf(userContext).planToRemoveShippingSpaceList(warehouse, shippingSpaceIds, allTokens());
				warehouse = saveWarehouse(userContext, warehouse, tokens().withShippingSpaceList().done());
				deleteRelationListInGraph(userContext, warehouse.getShippingSpaceList());
				return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			}
	}

	protected void checkParamsForRemovingShippingSpace(RetailscmUserContext userContext, String warehouseId,
		String shippingSpaceId, int shippingSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfShippingSpace(shippingSpaceId);
		checkerOf(userContext).checkVersionOfShippingSpace(shippingSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse removeShippingSpace(RetailscmUserContext userContext, String warehouseId,
		String shippingSpaceId, int shippingSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForRemovingShippingSpace(userContext,warehouseId, shippingSpaceId, shippingSpaceVersion,tokensExpr);

		ShippingSpace shippingSpace = createIndexedShippingSpace(shippingSpaceId, shippingSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.removeShippingSpace( shippingSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withShippingSpaceList().done());
			deleteRelationInGraph(userContext, shippingSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}


	}
	protected void checkParamsForCopyingShippingSpace(RetailscmUserContext userContext, String warehouseId,
		String shippingSpaceId, int shippingSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfShippingSpace(shippingSpaceId);
		checkerOf(userContext).checkVersionOfShippingSpace(shippingSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse copyShippingSpaceFrom(RetailscmUserContext userContext, String warehouseId,
		String shippingSpaceId, int shippingSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForCopyingShippingSpace(userContext,warehouseId, shippingSpaceId, shippingSpaceVersion,tokensExpr);

		ShippingSpace shippingSpace = createIndexedShippingSpace(shippingSpaceId, shippingSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation

			shippingSpace.updateLastUpdateTime(userContext.now());

			warehouse.copyShippingSpaceFrom( shippingSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withShippingSpaceList().done());
			
			shippingSpaceManagerOf(userContext).onNewInstanceCreated(userContext, (ShippingSpace)warehouse.getFlexiableObjects().get(BaseEntity.COPIED_CHILD));
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	protected void checkParamsForUpdatingShippingSpace(RetailscmUserContext userContext, String warehouseId, String shippingSpaceId, int shippingSpaceVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception{
		


		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfShippingSpace(shippingSpaceId);
		checkerOf(userContext).checkVersionOfShippingSpace(shippingSpaceVersion);


		if(ShippingSpace.LOCATION_PROPERTY.equals(property)){
			checkerOf(userContext).checkLocationOfShippingSpace(parseString(newValueExpr));
		}
		
		if(ShippingSpace.CONTACT_NUMBER_PROPERTY.equals(property)){
			checkerOf(userContext).checkContactNumberOfShippingSpace(parseString(newValueExpr));
		}
		
		if(ShippingSpace.TOTAL_AREA_PROPERTY.equals(property)){
			checkerOf(userContext).checkTotalAreaOfShippingSpace(parseString(newValueExpr));
		}
		
		if(ShippingSpace.LATITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLatitudeOfShippingSpace(parseBigDecimal(newValueExpr));
		}
		
		if(ShippingSpace.LONGITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLongitudeOfShippingSpace(parseBigDecimal(newValueExpr));
		}
		
		if(ShippingSpace.DESCRIPTION_PROPERTY.equals(property)){
			checkerOf(userContext).checkDescriptionOfShippingSpace(parseString(newValueExpr));
		}
		


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}

	public  Warehouse updateShippingSpace(RetailscmUserContext userContext, String warehouseId, String shippingSpaceId, int shippingSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
			throws Exception{

		checkParamsForUpdatingShippingSpace(userContext, warehouseId, shippingSpaceId, shippingSpaceVersion, property, newValueExpr,  tokensExpr);

		Map<String,Object> loadTokens = this.tokens().withShippingSpaceList().searchShippingSpaceListWith(ShippingSpace.ID_PROPERTY, tokens().equals(), shippingSpaceId).done();



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, loadTokens);

		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			//warehouse.removeShippingSpace( shippingSpace );
			//make changes to AcceleraterAccount.
			ShippingSpace shippingSpaceIdVersionKey = createIndexedShippingSpace(shippingSpaceId, shippingSpaceVersion);

			ShippingSpace shippingSpace = warehouse.findTheShippingSpace(shippingSpaceIdVersionKey);
			if(shippingSpace == null){
				throw new WarehouseManagerException(shippingSpaceId+" is NOT FOUND" );
			}

			beforeUpdateShippingSpace(userContext, shippingSpace, warehouseId, shippingSpaceId, shippingSpaceVersion, property, newValueExpr,  tokensExpr);
			shippingSpace.changeProperty(property, newValueExpr);
			shippingSpace.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().withShippingSpaceList().done());
			shippingSpaceManagerOf(userContext).onUpdated(userContext, shippingSpace, this, "updateShippingSpace");
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	/** if you has something need to do before update data from DB, override this */
	protected void beforeUpdateShippingSpace(RetailscmUserContext userContext, ShippingSpace existed, String warehouseId, String shippingSpaceId, int shippingSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
  			throws Exception{
  }
	/*

	*/




	protected void checkParamsForAddingDamageSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude,String [] tokensExpr) throws Exception{

				checkerOf(userContext).checkIdOfWarehouse(warehouseId);


		checkerOf(userContext).checkLocationOfDamageSpace(location);

		checkerOf(userContext).checkContactNumberOfDamageSpace(contactNumber);

		checkerOf(userContext).checkTotalAreaOfDamageSpace(totalArea);

		checkerOf(userContext).checkLatitudeOfDamageSpace(latitude);

		checkerOf(userContext).checkLongitudeOfDamageSpace(longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}
	public  Warehouse addDamageSpace(RetailscmUserContext userContext, String warehouseId, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForAddingDamageSpace(userContext,warehouseId,location, contactNumber, totalArea, latitude, longitude,tokensExpr);

		DamageSpace damageSpace = createDamageSpace(userContext,location, contactNumber, totalArea, latitude, longitude);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, emptyOptions());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.addDamageSpace( damageSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withDamageSpaceList().done());
			
			damageSpaceManagerOf(userContext).onNewInstanceCreated(userContext, damageSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}
	protected void checkParamsForUpdatingDamageSpaceProperties(RetailscmUserContext userContext, String warehouseId,String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude,String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfDamageSpace(id);

		checkerOf(userContext).checkLocationOfDamageSpace( location);
		checkerOf(userContext).checkContactNumberOfDamageSpace( contactNumber);
		checkerOf(userContext).checkTotalAreaOfDamageSpace( totalArea);
		checkerOf(userContext).checkLatitudeOfDamageSpace( latitude);
		checkerOf(userContext).checkLongitudeOfDamageSpace( longitude);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse updateDamageSpaceProperties(RetailscmUserContext userContext, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingDamageSpaceProperties(userContext,warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);

		Map<String, Object> options = tokens()
				.allTokens()
				//.withDamageSpaceListList()
				.searchDamageSpaceListWith(DamageSpace.ID_PROPERTY, tokens().is(), id).done();

		Warehouse warehouseToUpdate = loadWarehouse(userContext, warehouseId, options);

		if(warehouseToUpdate.getDamageSpaceList().isEmpty()){
			throw new WarehouseManagerException("DamageSpace is NOT FOUND with id: '"+id+"'");
		}

		DamageSpace item = warehouseToUpdate.getDamageSpaceList().first();
		beforeUpdateDamageSpaceProperties(userContext,item, warehouseId,id,location,contactNumber,totalArea,latitude,longitude,tokensExpr);
		item.updateLocation( location );
		item.updateContactNumber( contactNumber );
		item.updateTotalArea( totalArea );
		item.updateLatitude( latitude );
		item.updateLongitude( longitude );


		//checkParamsForAddingDamageSpace(userContext,warehouseId,name, code, used,tokensExpr);
		Warehouse warehouse = saveWarehouse(userContext, warehouseToUpdate, tokens().withDamageSpaceList().done());
		synchronized(warehouse){
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}

	protected  void beforeUpdateDamageSpaceProperties(RetailscmUserContext userContext, DamageSpace item, String warehouseId, String id,String location,String contactNumber,String totalArea,BigDecimal latitude,BigDecimal longitude, String [] tokensExpr)
						throws Exception {
			// by default, nothing to do
	}

	protected DamageSpace createDamageSpace(RetailscmUserContext userContext, String location, String contactNumber, String totalArea, BigDecimal latitude, BigDecimal longitude) throws Exception{

		DamageSpace damageSpace = new DamageSpace();
		
		
		damageSpace.setLocation(location);		
		damageSpace.setContactNumber(contactNumber);		
		damageSpace.setTotalArea(totalArea);		
		damageSpace.setLatitude(latitude);		
		damageSpace.setLongitude(longitude);		
		damageSpace.setLastUpdateTime(userContext.now());
	
		
		return damageSpace;


	}

	protected DamageSpace createIndexedDamageSpace(String id, int version){

		DamageSpace damageSpace = new DamageSpace();
		damageSpace.setId(id);
		damageSpace.setVersion(version);
		return damageSpace;

	}

	protected void checkParamsForRemovingDamageSpaceList(RetailscmUserContext userContext, String warehouseId,
			String damageSpaceIds[],String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		for(String damageSpaceIdItem: damageSpaceIds){
			checkerOf(userContext).checkIdOfDamageSpace(damageSpaceIdItem);
		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

	}
	public  Warehouse removeDamageSpaceList(RetailscmUserContext userContext, String warehouseId,
			String damageSpaceIds[],String [] tokensExpr) throws Exception{

			checkParamsForRemovingDamageSpaceList(userContext, warehouseId,  damageSpaceIds, tokensExpr);


			Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
			synchronized(warehouse){
				//Will be good when the warehouse loaded from this JVM process cache.
				//Also good when there is a RAM based DAO implementation
				warehouseDaoOf(userContext).planToRemoveDamageSpaceList(warehouse, damageSpaceIds, allTokens());
				warehouse = saveWarehouse(userContext, warehouse, tokens().withDamageSpaceList().done());
				deleteRelationListInGraph(userContext, warehouse.getDamageSpaceList());
				return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			}
	}

	protected void checkParamsForRemovingDamageSpace(RetailscmUserContext userContext, String warehouseId,
		String damageSpaceId, int damageSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfDamageSpace(damageSpaceId);
		checkerOf(userContext).checkVersionOfDamageSpace(damageSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse removeDamageSpace(RetailscmUserContext userContext, String warehouseId,
		String damageSpaceId, int damageSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForRemovingDamageSpace(userContext,warehouseId, damageSpaceId, damageSpaceVersion,tokensExpr);

		DamageSpace damageSpace = createIndexedDamageSpace(damageSpaceId, damageSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.removeDamageSpace( damageSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withDamageSpaceList().done());
			deleteRelationInGraph(userContext, damageSpace);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}


	}
	protected void checkParamsForCopyingDamageSpace(RetailscmUserContext userContext, String warehouseId,
		String damageSpaceId, int damageSpaceVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfDamageSpace(damageSpaceId);
		checkerOf(userContext).checkVersionOfDamageSpace(damageSpaceVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse copyDamageSpaceFrom(RetailscmUserContext userContext, String warehouseId,
		String damageSpaceId, int damageSpaceVersion,String [] tokensExpr) throws Exception{

		checkParamsForCopyingDamageSpace(userContext,warehouseId, damageSpaceId, damageSpaceVersion,tokensExpr);

		DamageSpace damageSpace = createIndexedDamageSpace(damageSpaceId, damageSpaceVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation

			damageSpace.updateLastUpdateTime(userContext.now());

			warehouse.copyDamageSpaceFrom( damageSpace );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withDamageSpaceList().done());
			
			damageSpaceManagerOf(userContext).onNewInstanceCreated(userContext, (DamageSpace)warehouse.getFlexiableObjects().get(BaseEntity.COPIED_CHILD));
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	protected void checkParamsForUpdatingDamageSpace(RetailscmUserContext userContext, String warehouseId, String damageSpaceId, int damageSpaceVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception{
		


		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfDamageSpace(damageSpaceId);
		checkerOf(userContext).checkVersionOfDamageSpace(damageSpaceVersion);


		if(DamageSpace.LOCATION_PROPERTY.equals(property)){
			checkerOf(userContext).checkLocationOfDamageSpace(parseString(newValueExpr));
		}
		
		if(DamageSpace.CONTACT_NUMBER_PROPERTY.equals(property)){
			checkerOf(userContext).checkContactNumberOfDamageSpace(parseString(newValueExpr));
		}
		
		if(DamageSpace.TOTAL_AREA_PROPERTY.equals(property)){
			checkerOf(userContext).checkTotalAreaOfDamageSpace(parseString(newValueExpr));
		}
		
		if(DamageSpace.LATITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLatitudeOfDamageSpace(parseBigDecimal(newValueExpr));
		}
		
		if(DamageSpace.LONGITUDE_PROPERTY.equals(property)){
			checkerOf(userContext).checkLongitudeOfDamageSpace(parseBigDecimal(newValueExpr));
		}
		


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}

	public  Warehouse updateDamageSpace(RetailscmUserContext userContext, String warehouseId, String damageSpaceId, int damageSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
			throws Exception{

		checkParamsForUpdatingDamageSpace(userContext, warehouseId, damageSpaceId, damageSpaceVersion, property, newValueExpr,  tokensExpr);

		Map<String,Object> loadTokens = this.tokens().withDamageSpaceList().searchDamageSpaceListWith(DamageSpace.ID_PROPERTY, tokens().equals(), damageSpaceId).done();



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, loadTokens);

		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			//warehouse.removeDamageSpace( damageSpace );
			//make changes to AcceleraterAccount.
			DamageSpace damageSpaceIdVersionKey = createIndexedDamageSpace(damageSpaceId, damageSpaceVersion);

			DamageSpace damageSpace = warehouse.findTheDamageSpace(damageSpaceIdVersionKey);
			if(damageSpace == null){
				throw new WarehouseManagerException(damageSpaceId+" is NOT FOUND" );
			}

			beforeUpdateDamageSpace(userContext, damageSpace, warehouseId, damageSpaceId, damageSpaceVersion, property, newValueExpr,  tokensExpr);
			damageSpace.changeProperty(property, newValueExpr);
			damageSpace.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().withDamageSpaceList().done());
			damageSpaceManagerOf(userContext).onUpdated(userContext, damageSpace, this, "updateDamageSpace");
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	/** if you has something need to do before update data from DB, override this */
	protected void beforeUpdateDamageSpace(RetailscmUserContext userContext, DamageSpace existed, String warehouseId, String damageSpaceId, int damageSpaceVersion, String property, String newValueExpr,String [] tokensExpr)
  			throws Exception{
  }
	/*

	*/




	protected void checkParamsForAddingWarehouseAsset(RetailscmUserContext userContext, String warehouseId, String name, String position,String [] tokensExpr) throws Exception{

				checkerOf(userContext).checkIdOfWarehouse(warehouseId);


		checkerOf(userContext).checkNameOfWarehouseAsset(name);

		checkerOf(userContext).checkPositionOfWarehouseAsset(position);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);



	}
	public  Warehouse addWarehouseAsset(RetailscmUserContext userContext, String warehouseId, String name, String position, String [] tokensExpr) throws Exception
	{
		checkParamsForAddingWarehouseAsset(userContext,warehouseId,name, position,tokensExpr);

		WarehouseAsset warehouseAsset = createWarehouseAsset(userContext,name, position);

		Warehouse warehouse = loadWarehouse(userContext, warehouseId, emptyOptions());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.addWarehouseAsset( warehouseAsset );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withWarehouseAssetList().done());
			
			warehouseAssetManagerOf(userContext).onNewInstanceCreated(userContext, warehouseAsset);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}
	protected void checkParamsForUpdatingWarehouseAssetProperties(RetailscmUserContext userContext, String warehouseId,String id,String name,String position,String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfWarehouseAsset(id);

		checkerOf(userContext).checkNameOfWarehouseAsset( name);
		checkerOf(userContext).checkPositionOfWarehouseAsset( position);


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse updateWarehouseAssetProperties(RetailscmUserContext userContext, String warehouseId, String id,String name,String position, String [] tokensExpr) throws Exception
	{
		checkParamsForUpdatingWarehouseAssetProperties(userContext,warehouseId,id,name,position,tokensExpr);

		Map<String, Object> options = tokens()
				.allTokens()
				//.withWarehouseAssetListList()
				.searchWarehouseAssetListWith(WarehouseAsset.ID_PROPERTY, tokens().is(), id).done();

		Warehouse warehouseToUpdate = loadWarehouse(userContext, warehouseId, options);

		if(warehouseToUpdate.getWarehouseAssetList().isEmpty()){
			throw new WarehouseManagerException("WarehouseAsset is NOT FOUND with id: '"+id+"'");
		}

		WarehouseAsset item = warehouseToUpdate.getWarehouseAssetList().first();
		beforeUpdateWarehouseAssetProperties(userContext,item, warehouseId,id,name,position,tokensExpr);
		item.updateName( name );
		item.updatePosition( position );


		//checkParamsForAddingWarehouseAsset(userContext,warehouseId,name, code, used,tokensExpr);
		Warehouse warehouse = saveWarehouse(userContext, warehouseToUpdate, tokens().withWarehouseAssetList().done());
		synchronized(warehouse){
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}
	}

	protected  void beforeUpdateWarehouseAssetProperties(RetailscmUserContext userContext, WarehouseAsset item, String warehouseId, String id,String name,String position, String [] tokensExpr)
						throws Exception {
			// by default, nothing to do
	}

	protected WarehouseAsset createWarehouseAsset(RetailscmUserContext userContext, String name, String position) throws Exception{

		WarehouseAsset warehouseAsset = new WarehouseAsset();
		
		
		warehouseAsset.setName(name);		
		warehouseAsset.setPosition(position);		
		warehouseAsset.setLastUpdateTime(userContext.now());
	
		
		return warehouseAsset;


	}

	protected WarehouseAsset createIndexedWarehouseAsset(String id, int version){

		WarehouseAsset warehouseAsset = new WarehouseAsset();
		warehouseAsset.setId(id);
		warehouseAsset.setVersion(version);
		return warehouseAsset;

	}

	protected void checkParamsForRemovingWarehouseAssetList(RetailscmUserContext userContext, String warehouseId,
			String warehouseAssetIds[],String [] tokensExpr) throws Exception {

		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		for(String warehouseAssetIdItem: warehouseAssetIds){
			checkerOf(userContext).checkIdOfWarehouseAsset(warehouseAssetIdItem);
		}


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);

	}
	public  Warehouse removeWarehouseAssetList(RetailscmUserContext userContext, String warehouseId,
			String warehouseAssetIds[],String [] tokensExpr) throws Exception{

			checkParamsForRemovingWarehouseAssetList(userContext, warehouseId,  warehouseAssetIds, tokensExpr);


			Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
			synchronized(warehouse){
				//Will be good when the warehouse loaded from this JVM process cache.
				//Also good when there is a RAM based DAO implementation
				warehouseDaoOf(userContext).planToRemoveWarehouseAssetList(warehouse, warehouseAssetIds, allTokens());
				warehouse = saveWarehouse(userContext, warehouse, tokens().withWarehouseAssetList().done());
				deleteRelationListInGraph(userContext, warehouse.getWarehouseAssetList());
				return present(userContext,warehouse, mergedAllTokens(tokensExpr));
			}
	}

	protected void checkParamsForRemovingWarehouseAsset(RetailscmUserContext userContext, String warehouseId,
		String warehouseAssetId, int warehouseAssetVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfWarehouseAsset(warehouseAssetId);
		checkerOf(userContext).checkVersionOfWarehouseAsset(warehouseAssetVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse removeWarehouseAsset(RetailscmUserContext userContext, String warehouseId,
		String warehouseAssetId, int warehouseAssetVersion,String [] tokensExpr) throws Exception{

		checkParamsForRemovingWarehouseAsset(userContext,warehouseId, warehouseAssetId, warehouseAssetVersion,tokensExpr);

		WarehouseAsset warehouseAsset = createIndexedWarehouseAsset(warehouseAssetId, warehouseAssetVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			warehouse.removeWarehouseAsset( warehouseAsset );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withWarehouseAssetList().done());
			deleteRelationInGraph(userContext, warehouseAsset);
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}


	}
	protected void checkParamsForCopyingWarehouseAsset(RetailscmUserContext userContext, String warehouseId,
		String warehouseAssetId, int warehouseAssetVersion,String [] tokensExpr) throws Exception{
		
		checkerOf(userContext).checkIdOfWarehouse( warehouseId);
		checkerOf(userContext).checkIdOfWarehouseAsset(warehouseAssetId);
		checkerOf(userContext).checkVersionOfWarehouseAsset(warehouseAssetVersion);

		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}
	public  Warehouse copyWarehouseAssetFrom(RetailscmUserContext userContext, String warehouseId,
		String warehouseAssetId, int warehouseAssetVersion,String [] tokensExpr) throws Exception{

		checkParamsForCopyingWarehouseAsset(userContext,warehouseId, warehouseAssetId, warehouseAssetVersion,tokensExpr);

		WarehouseAsset warehouseAsset = createIndexedWarehouseAsset(warehouseAssetId, warehouseAssetVersion);
		Warehouse warehouse = loadWarehouse(userContext, warehouseId, allTokens());
		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation

			warehouseAsset.updateLastUpdateTime(userContext.now());

			warehouse.copyWarehouseAssetFrom( warehouseAsset );
			warehouse = saveWarehouse(userContext, warehouse, tokens().withWarehouseAssetList().done());
			
			warehouseAssetManagerOf(userContext).onNewInstanceCreated(userContext, (WarehouseAsset)warehouse.getFlexiableObjects().get(BaseEntity.COPIED_CHILD));
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	protected void checkParamsForUpdatingWarehouseAsset(RetailscmUserContext userContext, String warehouseId, String warehouseAssetId, int warehouseAssetVersion, String property, String newValueExpr,String [] tokensExpr) throws Exception{
		


		checkerOf(userContext).checkIdOfWarehouse(warehouseId);
		checkerOf(userContext).checkIdOfWarehouseAsset(warehouseAssetId);
		checkerOf(userContext).checkVersionOfWarehouseAsset(warehouseAssetVersion);


		if(WarehouseAsset.NAME_PROPERTY.equals(property)){
			checkerOf(userContext).checkNameOfWarehouseAsset(parseString(newValueExpr));
		}
		
		if(WarehouseAsset.POSITION_PROPERTY.equals(property)){
			checkerOf(userContext).checkPositionOfWarehouseAsset(parseString(newValueExpr));
		}
		


		checkerOf(userContext).throwExceptionIfHasErrors(WarehouseManagerException.class);


	}

	public  Warehouse updateWarehouseAsset(RetailscmUserContext userContext, String warehouseId, String warehouseAssetId, int warehouseAssetVersion, String property, String newValueExpr,String [] tokensExpr)
			throws Exception{

		checkParamsForUpdatingWarehouseAsset(userContext, warehouseId, warehouseAssetId, warehouseAssetVersion, property, newValueExpr,  tokensExpr);

		Map<String,Object> loadTokens = this.tokens().withWarehouseAssetList().searchWarehouseAssetListWith(WarehouseAsset.ID_PROPERTY, tokens().equals(), warehouseAssetId).done();



		Warehouse warehouse = loadWarehouse(userContext, warehouseId, loadTokens);

		synchronized(warehouse){
			//Will be good when the warehouse loaded from this JVM process cache.
			//Also good when there is a RAM based DAO implementation
			//warehouse.removeWarehouseAsset( warehouseAsset );
			//make changes to AcceleraterAccount.
			WarehouseAsset warehouseAssetIdVersionKey = createIndexedWarehouseAsset(warehouseAssetId, warehouseAssetVersion);

			WarehouseAsset warehouseAsset = warehouse.findTheWarehouseAsset(warehouseAssetIdVersionKey);
			if(warehouseAsset == null){
				throw new WarehouseManagerException(warehouseAssetId+" is NOT FOUND" );
			}

			beforeUpdateWarehouseAsset(userContext, warehouseAsset, warehouseId, warehouseAssetId, warehouseAssetVersion, property, newValueExpr,  tokensExpr);
			warehouseAsset.changeProperty(property, newValueExpr);
			warehouseAsset.updateLastUpdateTime(userContext.now());
			warehouse = saveWarehouse(userContext, warehouse, tokens().withWarehouseAssetList().done());
			warehouseAssetManagerOf(userContext).onUpdated(userContext, warehouseAsset, this, "updateWarehouseAsset");
			return present(userContext,warehouse, mergedAllTokens(tokensExpr));
		}

	}

	/** if you has something need to do before update data from DB, override this */
	protected void beforeUpdateWarehouseAsset(RetailscmUserContext userContext, WarehouseAsset existed, String warehouseId, String warehouseAssetId, int warehouseAssetVersion, String property, String newValueExpr,String [] tokensExpr)
  			throws Exception{
  }
	/*

	*/




	public void onNewInstanceCreated(RetailscmUserContext userContext, Warehouse newCreated) throws Exception{
		ensureRelationInGraph(userContext, newCreated);
		sendCreationEvent(userContext, newCreated);

    
	}

  
  

  public void sendAllItems(RetailscmUserContext ctx) throws Exception{
    warehouseDaoOf(ctx).loadAllAsStream().forEach(
          event -> sendInitEvent(ctx, event)
    );
  }



	// -----------------------------------//  登录部分处理 \\-----------------------------------
	@Override
  protected BusinessHandler getLoginProcessBizHandler(RetailscmUserContextImpl userContext) {
    return this;
  }

	public void onAuthenticationFailed(RetailscmUserContext userContext, LoginContext loginContext,
			LoginResult loginResult, IdentificationHandler idHandler, BusinessHandler bizHandler)
			throws Exception {
		// by default, failed is failed, nothing can do
	}
	// when user authenticated success, but no sec_user related, this maybe a new user login from 3-rd party service.
	public void onAuthenticateNewUserLogged(RetailscmUserContext userContext, LoginContext loginContext,
			LoginResult loginResult, IdentificationHandler idHandler, BusinessHandler bizHandler)
			throws Exception {
		// Generally speaking, when authenticated user logined, we will create a new account for him/her.
		// you need do it like :
		// First, you should create new data such as:
		//   Warehouse newWarehouse = this.createWarehouse(userContext, ...
		// Next, create a sec-user in your business way:
		//   SecUser secUser = secUserManagerOf(userContext).createSecUser(userContext, login, mobile ...
		// And set it into loginContext:
		//   loginContext.getLoginTarget().setSecUser(secUser);
		// Next, create an user-app to connect secUser and newWarehouse
		//   UserApp uerApp = userAppManagerOf(userContext).createUserApp(userContext, secUser.getId(), ...
		// Also, set it into loginContext:
		//   loginContext.getLoginTarget().setUserApp(userApp);
		// and in most case, this should be considered as "login success"
		//   loginResult.setSuccess(true);
		//
		// Since many of detailed info were depending business requirement, So,
		throw new Exception("请重载函数onAuthenticateNewUserLogged()以处理新用户登录");
	}
	protected SmartList<UserApp> getRelatedUserAppList(RetailscmUserContext userContext, SecUser secUser) {
    MultipleAccessKey key = new MultipleAccessKey();
    key.put(UserApp.SEC_USER_PROPERTY, secUser.getId());
    key.put(UserApp.APP_TYPE_PROPERTY, Warehouse.INTERNAL_TYPE);
    SmartList<UserApp> userApps = userContext.getDAOGroup().getUserAppDAO().findUserAppWithKey(key, EO);
    return userApps;
  }
	// -----------------------------------\\  登录部分处理 //-----------------------------------



	// -----------------------------------// list-of-view 处理 \\-----------------------------------
    protected void enhanceForListOfView(RetailscmUserContext userContext,SmartList<Warehouse> list) throws Exception {
    	if (list == null || list.isEmpty()){
    		return;
    	}
		List<RetailStoreCountryCenter> ownerList = RetailscmBaseUtils.collectReferencedObjectWithType(userContext, list, RetailStoreCountryCenter.class);
		userContext.getDAOGroup().enhanceList(ownerList, RetailStoreCountryCenter.class);


    }
	
	public Object listByOwner(RetailscmUserContext userContext,String ownerId) throws Exception {
		return listPageByOwner(userContext, ownerId, 0, 20);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object listPageByOwner(RetailscmUserContext userContext,String ownerId, int start, int count) throws Exception {
		SmartList<Warehouse> list = warehouseDaoOf(userContext).findWarehouseByOwner(ownerId, start, count, new HashMap<>());
		enhanceForListOfView(userContext, list);
		RetailscmCommonListOfViewPage page = new RetailscmCommonListOfViewPage();
		page.setClassOfList(Warehouse.class);
		page.setContainerObject(RetailStoreCountryCenter.withId(ownerId));
		page.setRequestBeanName(this.getBeanName());
		page.setDataList((SmartList)list);
		page.setPageTitle("仓库列表");
		page.setRequestName("listByOwner");
		page.setRequestOffset(start);
		page.setRequestLimit(count);
		page.setDisplayMode("document");
		page.setLinkToUrl(TextUtil.encodeUrl(String.format("%s/listByOwner/%s/",  getBeanName(), ownerId)));

		page.assemblerContent(userContext, "listByOwner");
		return page.doRender(userContext);
	}
  
  // -----------------------------------\\ list-of-view 处理 //-----------------------------------v
  
 	/**
	 * miniprogram调用返回固定的detail class
	 *
	 * @return
	 * @throws Exception
	 */
 	public Object wxappview(RetailscmUserContext userContext, String warehouseId) throws Exception{
    SerializeScope vscope = SerializeScope.EXCLUDE().nothing();
		Warehouse merchantObj = (Warehouse) this.view(userContext, warehouseId);
    String merchantObjId = warehouseId;
    String linkToUrl =	"warehouseManager/wxappview/" + merchantObjId + "/";
    String pageTitle = "仓库"+"详情";
		Map result = new HashMap();
		List propList = new ArrayList();
		List sections = new ArrayList();
 
		propList.add(
				MapUtil.put("id", "1-id")
				    .put("fieldName", "id")
				    .put("label", "ID")
				    .put("type", "text")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("id", merchantObj.getId());

		propList.add(
				MapUtil.put("id", "2-location")
				    .put("fieldName", "location")
				    .put("label", "位置")
				    .put("type", "text")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("location", merchantObj.getLocation());

		propList.add(
				MapUtil.put("id", "3-contactNumber")
				    .put("fieldName", "contactNumber")
				    .put("label", "联系电话")
				    .put("type", "text")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("contactNumber", merchantObj.getContactNumber());

		propList.add(
				MapUtil.put("id", "4-totalArea")
				    .put("fieldName", "totalArea")
				    .put("label", "总面积")
				    .put("type", "text")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("totalArea", merchantObj.getTotalArea());

		propList.add(
				MapUtil.put("id", "5-owner")
				    .put("fieldName", "owner")
				    .put("label", "业主")
				    .put("type", "auto")
				    .put("linkToUrl", "retailStoreCountryCenterManager/wxappview/:id/")
				    .put("displayMode", "{\"brief\":\"description\",\"imageUrl\":\"\",\"name\":\"auto\",\"title\":\"name\",\"imageList\":\"\"}")
				    .into_map()
		);
		result.put("owner", merchantObj.getOwner());

		propList.add(
				MapUtil.put("id", "6-latitude")
				    .put("fieldName", "latitude")
				    .put("label", "纬度")
				    .put("type", "text")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("latitude", merchantObj.getLatitude());

		propList.add(
				MapUtil.put("id", "7-longitude")
				    .put("fieldName", "longitude")
				    .put("label", "经度")
				    .put("type", "text")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("longitude", merchantObj.getLongitude());

		propList.add(
				MapUtil.put("id", "8-contract")
				    .put("fieldName", "contract")
				    .put("label", "合同")
				    .put("type", "document")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("contract", merchantObj.getContract());

		propList.add(
				MapUtil.put("id", "9-lastUpdateTime")
				    .put("fieldName", "lastUpdateTime")
				    .put("label", "更新于")
				    .put("type", "datetime")
				    .put("linkToUrl", "")
				    .put("displayMode", "{}")
				    .into_map()
		);
		result.put("lastUpdateTime", merchantObj.getLastUpdateTime());

		//处理 sectionList

		//处理Section：storageSpaceListSection
		Map storageSpaceListSection = ListofUtils.buildSection(
		    "storageSpaceListSection",
		    "存储空间列表",
		    null,
		    "",
		    "__no_group",
		    "storageSpaceManager/listByWarehouse/"+merchantObjId+"/",
		    "auto"
		);
		sections.add(storageSpaceListSection);

		result.put("storageSpaceListSection", ListofUtils.toShortList(merchantObj.getStorageSpaceList(), "storageSpace"));

		//处理Section：smartPalletListSection
		Map smartPalletListSection = ListofUtils.buildSection(
		    "smartPalletListSection",
		    "智能托盘清单",
		    null,
		    "",
		    "__no_group",
		    "smartPalletManager/listByWarehouse/"+merchantObjId+"/",
		    "auto"
		);
		sections.add(smartPalletListSection);

		result.put("smartPalletListSection", ListofUtils.toShortList(merchantObj.getSmartPalletList(), "smartPallet"));

		//处理Section：supplierSpaceListSection
		Map supplierSpaceListSection = ListofUtils.buildSection(
		    "supplierSpaceListSection",
		    "供应商空间列表",
		    null,
		    "",
		    "__no_group",
		    "supplierSpaceManager/listByWarehouse/"+merchantObjId+"/",
		    "auto"
		);
		sections.add(supplierSpaceListSection);

		result.put("supplierSpaceListSection", ListofUtils.toShortList(merchantObj.getSupplierSpaceList(), "supplierSpace"));

		//处理Section：receivingSpaceListSection
		Map receivingSpaceListSection = ListofUtils.buildSection(
		    "receivingSpaceListSection",
		    "接收空间列表",
		    null,
		    "",
		    "__no_group",
		    "receivingSpaceManager/listByWarehouse/"+merchantObjId+"/",
		    "auto"
		);
		sections.add(receivingSpaceListSection);

		result.put("receivingSpaceListSection", ListofUtils.toShortList(merchantObj.getReceivingSpaceList(), "receivingSpace"));

		//处理Section：shippingSpaceListSection
		Map shippingSpaceListSection = ListofUtils.buildSection(
		    "shippingSpaceListSection",
		    "舱位列表",
		    null,
		    "",
		    "__no_group",
		    "shippingSpaceManager/listByWarehouse/"+merchantObjId+"/",
		    "auto"
		);
		sections.add(shippingSpaceListSection);

		result.put("shippingSpaceListSection", ListofUtils.toShortList(merchantObj.getShippingSpaceList(), "shippingSpace"));

		//处理Section：damageSpaceListSection
		Map damageSpaceListSection = ListofUtils.buildSection(
		    "damageSpaceListSection",
		    "破坏空间列表",
		    null,
		    "",
		    "__no_group",
		    "damageSpaceManager/listByWarehouse/"+merchantObjId+"/",
		    "auto"
		);
		sections.add(damageSpaceListSection);

		result.put("damageSpaceListSection", ListofUtils.toShortList(merchantObj.getDamageSpaceList(), "damageSpace"));

		//处理Section：warehouseAssetListSection
		Map warehouseAssetListSection = ListofUtils.buildSection(
		    "warehouseAssetListSection",
		    "仓库资产列表",
		    null,
		    "",
		    "__no_group",
		    "warehouseAssetManager/listByOwner/"+merchantObjId+"/",
		    "auto"
		);
		sections.add(warehouseAssetListSection);

		result.put("warehouseAssetListSection", ListofUtils.toShortList(merchantObj.getWarehouseAssetList(), "warehouseAsset"));

		result.put("propList", propList);
		result.put("sectionList", sections);
		result.put("pageTitle", pageTitle);
		result.put("linkToUrl", linkToUrl);

		vscope.field("propList", SerializeScope.EXCLUDE())
				.field("sectionList", SerializeScope.EXCLUDE())
				.field("pageTitle", SerializeScope.EXCLUDE())
				.field("linkToUrl", SerializeScope.EXCLUDE());
		userContext.forceResponseXClassHeader("com.terapico.appview.DetailPage");
		return BaseViewPage.serialize(result, vscope);
	}

  










}




