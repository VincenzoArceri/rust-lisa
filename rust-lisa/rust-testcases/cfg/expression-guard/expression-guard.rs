fn main() {
    let x = 42;
    let y = 2;
    let z = false;
    let w = &42;

    if x > y { x = 1; }
    if x < y { x = 1; }
    if x == y { x = 1; }
    if x >= y { x = 1; }
    if x <= y { x = 1; }
    if z && z { x = 1; }
    if z || z { x = 1; }
    if !z { x = 1; }
    if &x == w { x = 1; }
    if &&x == 0 { x = 1; }
    if *x == 0 { x = 1; }
    if x as i32 == 0{ x = 1; }
    if box x == 0 { x = 1; }
    if x ^ y == 0 { x = 1; }
    if x | y == 0 { x = 1; }
    if x & y == 0 { x = 1; }
    if x >> y == 0 { x = 1; }
    if x << y == 0 { x = 1; }
    if x + y == 0 { x = 1; }
    if x - y == 0 { x = 1; }
    if x * y == 0 { x = 1; }
    if x / y == 0 { x = 1; }
    if x % y == 0 { x = 1; }
 }
