#include  <msp430g2553.h>

#define RXD        BIT1
#define TXD        BIT2
#define BUTTON     BIT3

//------------------ Method declarations------------------------//
void UARTTransmit(char * tx_data);
char* UARTReceive();
unsigned char getReceivedChar();
char* UARTReceive2();
void setUp();

void main(void)
{
  setUp();
}

void setUp(){
  //------------------- Configure the UART  -------------------//
  WDTCTL = WDTPW + WDTHOLD;         // Stop Watch dog timer
  BCSCTL1 = CALBC1_1MHZ;            // Set DCO to 1 MHz
  DCOCTL = CALDCO_1MHZ;
  P1DIR &=~BUTTON;                  // Ensure button is input (sets a 0 in P1DIR register at location BIT3)
  P1OUT |=  BUTTON;                 // Enables pullup resistor on button
  P1REN |=  BUTTON;
  P1SEL = RXD + TXD ;                // Select TX and RX functionality for P1.1 & P1.2
  P1SEL2 = RXD + TXD ;              //
  UCA0CTL1 |= UCSSEL_2;             // Have USCI use System Master Clock: AKA core clk 1MHz
  UCA0BR0 = 104;                    // 1MHz 9600, see user manual
  UCA0BR1 = 0;                      //
  UCA0MCTL = UCBRS0;                // Modulation UCBRSx = 1
  UCA0CTL1 &= ~UCSWRST;             // Start USCI state machine

  //---------------- Configuring the LED's ----------------------//

  P1DIR  |=  BIT0 + BIT6;  // P1.0 and P1.6 output
  P1OUT  &= ~BIT0 + BIT6;  // P1.0 and P1.6 = 0

  //---------------- Enabling the interrupts ------------------//
  IE2 |= UCA0TXIE;                  // Enable the Transmit interrupt
  IE2 |= UCA0RXIE;                  // Enable the Receive  interrupt
  _BIS_SR(GIE);                     // Enable the global interrupt

}

void UARTTransmit(char * tx_data){
  P1OUT  ^= BIT0;//light up P1.0 Led on Tx
  unsigned int i=0;
  while(tx_data[i]) //Increment through array, look for null pointer (0) at end of string
  {
    while ((UCA0STAT & UCBUSY)); //Wait if line TX/RX module is busy with data
    UCA0TXBUF = tx_data[i]; //Send out element i of tx_data array on UART bus
    i++;
  }
}

unsigned char getReceivedChar(void){
   while(!(IFG2 & UCA0RXIFG));
   IFG2 &= ~UCA0RXIFG;
   return UCA0RXBUF;
}

char* UARTReceive(){
  char received = '\n';
  char response[] = "" ;
  while(!(received == '\n')){
     received = getReceivedChar();
     strncat(response, &received, 1);
  }
  P1OUT ^= BIT6;     //light up P1.6 LED on RX
  __delay_cycles(1000000);
  IFG2 &= ~UCA0RXIFG; //Clear RX flag
  return response;
}

//---------Transmit and Receive interrupts-------//

#pragma vector = USCIAB0TX_VECTOR
__interrupt void TransmitInterrupt(void) {
    if(!((P1IN & BUTTON)==BUTTON)) {//If button is pressed
        UARTTransmit("Sample message. \r\n");
        __delay_cycles(1000000); //Debounce button so signal is not sent multiple time
    }
}

#pragma vector = USCIAB0RX_VECTOR
__interrupt void ReceiveInterrupt(void) {
   UARTReceive();
   __bic_SR_register_on_exit(CPUOFF);
}

