definition(
    name: "[011]Phát hiện mở cửa thì bật báo động 2 bước",
    namespace: "VTMS",
    author: "Võ Thanh Minh",
    description: "Đặt giờ mà hệ thống kiểm tra mức độ an toàn",
    category: "Safety & Security",
   iconUrl: "https://i.imgur.com/f73vWMD.png",
    iconX2Url: "https://i.imgur.com/f73vWMD.png",
    iconX3Url: "https://i.imgur.com/f73vWMD.png")

preferences 
{
 		input name:"sel",type:"enum", title:"Chọn ON để kích hoạt kịch bản", options: ["on","off"], defaultValue:"on"
        input name: "timeB", type: "time", title: "Giờ bắt đầu",defaultValue:"23:59"
        input name: "timeE", type: "time", title: "Và giờ kết thúc",defaultValue:"04:00"
        input("cs1","capability.contactSensor",title:"Tại cảm biến đóng, mở")
        input name: "tp1", type: "number", title: "Báo động lần 1 trong bao nhiêu giây", defaultValue:"20"
        input name:"typ1",type:"enum", title:"Với kiểu báo động lần 1: A: Còi hú; L: Đèn nhấp nháy", options: ["A","L"], defaultValue:"L"
        input name: "tp", type: "number", title: "Sau bao nhiêu(giây) thì kiểm tra lần 2 kể từ khi kết thúc lần 1?", defaultValue:"180"
        input name: "tp2", type: "number", title: "Báo động lần 2 trong bao nhiêu giây?", defaultValue:"20"
        input name:"typ2",type:"enum", title:"Với kiểu báo động lần 2: A: Còi hú; L: Đèn nhấp nháy", options: ["A","L"], defaultValue:"L"
     	input("alamH","capability.alarm",title:"Thiết bị nào phát âm thanh")
       
 	
      
}
def installed()
{
	init() 
}
def updated() 
{ 
	init()
}

def init()
{
    subscribe(cs1,"contact",cs_1)
    subscribe(alamH,"alarm",alam_H)
}

def cs_1(evt)
{
    def timeofB = timeToday(timeB)
    def timeofE= timeToday(timeE)
    def timeC=now()
    
    def p1= tp1*1000
	def p=	tp*1000
	
	
    def dk1= (timeofB<timeofE) && (timeC >= timeofB.time && timeC<=timeofE.time)
    def dk2= (timeofB>timeofE) && (timeC >= timeofB.time || timeC<=timeofE.time)
    
    if (evt.value == "open" && sel == "on")
    {
        if (dk1 || dk2)
        {
        	     sendPush("Báo động bước 1, Kiểu báo động ${typ1}, Lý do:${evt.displayName} phát hiện bị mở.")
           
           if(typ1=="L") 
            {
           		alamH.strobe()
        		schedule(now()+p1,alamF) // turn off in 10 second
                schedule(now()+p1+p,laplai)
        	}
            if(typ1=="A")
            {	
        		alamH.siren()
        		schedule(now()+p1,alamF) // turn off in 10 second
                schedule(now()+p+p1,laplai)
        	}
        	if(typ1=="AL")
        	{	
        		alamH.both()
           		schedule(now()+p1,alamF) // turn off in 10 second
                schedule(now()+p+p1,laplai)
        	}
		}
	    }
 }
 
def laplai()
{
	def timeofB = timeToday(timeB)
    def timeofE= timeToday(timeE)
    def timeC=now()
    def p2= tp2*1000
    def t_c = cs1.currentValue("contact")
    
	if (t_c == "open")
	{	
    		
        	if(typ2=="L") 
            {
           		alamH.strobe()
        		schedule(now()+p2,alamF) // turn off in 10 second
        	}
            if(typ2=="A")
            {
        		alamH.siren()
        		schedule(now()+p2,alamF) // turn off in 10 second
    
        	}
        	if(typ2=="AL")
        	{	
        		alamH.both()
           		schedule(now()+p2,alamF) // turn off in 10 second
    
        	}
	}
}
def alamF()
{
	alamH.off()
    
}