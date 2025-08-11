\ Define the pixel gradient string (from lightest to darkest)
create charset 41 c, ," .tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$"

\ Fetch character from charset given an index
: get-char ( idx -- c )  charset + c@ ;

\ Complex number square: (r i -- r^2 i^2 r*i)
: csquare ( r i -- r2 i2 ri )
  2dup * swap       \ r r i -> r2 i r
  >r dup *          \ r2 i2
  r> * ;            \ r2 i2 ri

\ Absolute value squared of complex number: (r i -- r^2 + i^2)
: cabs2 ( r i -- n )
  2dup * swap
  dup * +
;

\ Map pixel (px py size -- x y)
: map-pixel ( px py size -- x y )
  dup s>f           \ px py size sizef
  2dup f/           \ px py size xscale yscale
  rot s>f f* -1.75e f+    \ py size yscale x
  -rot s>f f* -1.12e f+ ; \ x y

\ Iteration function: (cr ci -- iter)
: mandel-point ( cr ci -- iter )
  0e 0e             \ cr ci zr zi
  0                 \ iter
  begin
    dup 100 <       \ max iterations
    swap            \ move iter to TOS
    >r              \ save iter
    2over 2over     \ zr zi zr zi
    csquare         \ zr2 zi2 zrzi
    -rot f-         \ zr2-zi2 zrzi
    over over f* f2* \ 2*zr*zi
    f+              \ zr2-zi2+cr 2*zr*zi+ci
    2swap drop drop \ update zr zi
    2dup cabs2 4e f< and
  while
    r> 1+           \ iter++
  repeat
  rdrop
  drop drop ;

\ Map iter to char: (iter -- char)
: iter>char
  dup 69 >= if drop 69 then
  get-char ;

\ Draw Mandelbrot set: (size --)
: mandelbrot ( size -- )
  dup 0 do
    dup 0 do
      j i over map-pixel      \ cr ci
      mandel-point            \ iter
      iter>char emit
    loop
    cr
  loop
  drop ;
